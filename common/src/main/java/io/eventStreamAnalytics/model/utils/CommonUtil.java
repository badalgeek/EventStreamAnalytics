package io.eventStreamAnalytics.model.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Created by sandeep on 24/1/16.
 */
public class CommonUtil {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(CommonUtil.class);
    public static <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) throws IOException {
        T data = null;
        try {
            data = new ObjectMapper().readValue(jsonPacket, type);
        } catch (IOException e) {
            logger.error("Failed to map:", e);
            throw e;
        }
        return data;
    }
}
