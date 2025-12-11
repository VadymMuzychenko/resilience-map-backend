package com.example.resiliencemap.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
public class GenericSmsPacker {

    private static final int SMS_LIMIT = 160;

    @Value("${app.android-hash}")
    private String androidHash;


    public <T> String packList(
            List<T> items,
            String typePrefix,
            int nextPageNum,
            Function<T, String> itemSerializer
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append(typePrefix);
        int reservedSpace = 4 + 1 + androidHash.length() + 5; // "<#> " + " " + HASH (11) + "~>>99" = 21

        int effectiveLimit = SMS_LIMIT - reservedSpace;

        boolean isFirst = true;
        boolean hasMoreData = false;

        for (T item : items) {
            String itemStr = itemSerializer.apply(item);
            int delimiterLen = isFirst ? 0 : 1; // "~"

            if (sb.length() + delimiterLen + itemStr.length() <= effectiveLimit) {
                if (!isFirst) sb.append("~");
                sb.append(itemStr);
                isFirst = false;
            } else {
                hasMoreData = true;
                break;
            }
        }

        if (hasMoreData || nextPageNum > 0) {
            int page = (nextPageNum == 0) ? 1 : nextPageNum;
            sb.append("~>>").append(page);
        }

        return sb.toString();
    }
}
