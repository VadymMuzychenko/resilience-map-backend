package com.example.resiliencemap.sms.handler;

import com.example.resiliencemap.core.aidpoint.AidPointRepository;
import com.example.resiliencemap.core.aidpoint.model.AidPoint;
import com.example.resiliencemap.core.aidpoint.model.LocationCoordinates;
import com.example.resiliencemap.functional.utils.SmsUtil;
import com.example.resiliencemap.sms.GenericSmsPacker;
import com.example.resiliencemap.sms.model.AidPointsSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetPointsHandler implements CommandHandler {

    private final AidPointRepository aidPointRepository;
    private final GenericSmsPacker genericSmsPacker;

    @Override
    public String getTriggerCommand() {
        return "GP";
    }

    @Transactional(readOnly = true)
    @Override
    public String handle(String phone, String rawSmsText) {
        AidPointsSearchRequest request = parseSearch(rawSmsText);
        Pageable pageable = PageRequest.of(request.getPage(), 3); // more than 3 elements will not fit in an SMS
        List<AidPoint> points;
        if (request.getLocationType() != null && request.getServices() != null && !request.getServices().isEmpty()) {
            String servicesString = String.join(",", request.getServices());
            points = aidPointRepository.findAidPointsWithinRadiusForUser(request.getLon(),
                    request.getLat(), 30000, request.getLocationType(), servicesString, pageable);
        } else if (request.getLocationType() == null && request.getServices() != null && !request.getServices().isEmpty()) {
            points = aidPointRepository.findAidPointsWithinRadiusForUser(request.getLon(),
                    request.getLat(), 30000, request.getServices(), pageable);
        } else if (request.getLocationType() != null) {
            points = aidPointRepository.findAidPointsWithinRadiusForUser(request.getLon(),
                    request.getLat(), 30000, request.getLocationType(), pageable);
        } else {
            points = aidPointRepository.findAidPointsWithinRadiusForUser(request.getLon(), request.getLat(), 30000, pageable);
        }

        int nextPage = 1;

        return genericSmsPacker.packList(
                points,
                "L|",
                nextPage,
                this::serializePoint
        );
    }

    private String serializePoint(AidPoint p) {
        LocationCoordinates coordinates = new LocationCoordinates(p.getLocation());
        return String.format("%d|%s|%.4f|%.4f|%s",
                p.getId(), p.getLocationType().getSmsCode(), coordinates.getLatitude(), coordinates.getLongitude(),
                SmsUtil.toLatin(p.getName())
        );
    }

    private AidPointsSearchRequest parseSearch(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            throw new IllegalArgumentException("Empty SMS");
        }
        String[] parts = rawText.split("\\|", -1);
        if (parts.length < 2 || !parts[0].equals("GP")) {
            throw new IllegalArgumentException("Invalid format. Start with GP");
        }
        LocationCoordinates coordinates = parseLocationCoordinates(parts, 1);
        String locationType = parseLocationType(parts, 2);
        Set<String> services = parseServices(parts, 3);
        int page = parsePage(parts, 4);
        return new AidPointsSearchRequest(coordinates.getLatitude(), coordinates.getLongitude(), locationType, services, page);
    }

    private int parsePage(String[] parts, int position) {
        int page = 0;
        if (parts.length > position && !parts[position].isBlank()) {
            try {
                page = Integer.parseInt(parts[position].trim());
                if (page < 0) page = 0;
            } catch (NumberFormatException e) {
                page = 0;
            }
        }
        return page;
    }

    private Set<String> parseServices(String[] parts, int position) {
        Set<String> services = Collections.emptySet();
        if (parts.length > position && !parts[position].isBlank()) {
            String val = parts[position].trim();
            if (!val.equals("*")) {
                services = Set.of(val.split("\\s*,\\s*"));
            }
        }
        return services;
    }

    private String parseLocationType(String[] parts, int position) {
        String locationType = null;
        if (parts.length > position && !parts[position].isBlank()) {
            String val = parts[position].trim();
            if (!val.equals("*")) {
                locationType = val;
            }
        }
        return locationType;
    }

    private LocationCoordinates parseLocationCoordinates(String[] parts, int position) {
        LocationCoordinates coordinates = new LocationCoordinates();
        try {
            String[] coords = parts[position].split(",");
            if (coords.length != 2)
                throw new Exception();
            coordinates.setLatitude(Double.parseDouble(coords[0]));
            coordinates.setLongitude(Double.parseDouble(coords[1]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        return coordinates;
    }
}
