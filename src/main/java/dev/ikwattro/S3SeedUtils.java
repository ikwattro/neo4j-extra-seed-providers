package dev.ikwattro;

import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.regions.Region;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class S3SeedUtils {

    private static final String SCHEME = "aws-s3://";

    public static S3SeedLocation locationFromURI(String originalUri) {
        var withQueryParams = Arrays.asList(originalUri.split("\\?"));
        var uri = withQueryParams.get(0);
        var reduced = uri.replace(SCHEME, "");
        var parts = Arrays.asList(reduced.split("/"));
        var bucket = parts.get(0);
        var fileKey = parts.get(parts.size()-1);
        var key = StringUtils.join(parts.subList(1, parts.size()), "/");

        if (!key.endsWith(".backup") && !key.endsWith(".dump")) {
            throw new IllegalArgumentException("Invalid backup key : %s. It must end with `.backup` or `.dump`".formatted(key));
        }

        if (withQueryParams.size() == 1) {
            return new S3SeedLocation(bucket, key, fileKey, Region.EU_WEST_1);
        }

        var params = Arrays.asList(withQueryParams.get(1).split("&"));
        Map<String, Object> queryParams = new HashMap<>();
        params.forEach(p -> {
            var vals = Arrays.asList(p.split("="));
            queryParams.put(vals.get(0), vals.get(1));
        });

        if (queryParams.containsKey("region")) {
            return new S3SeedLocation(bucket, key, fileKey, Region.of(queryParams.get("region").toString()));
        }

        return new S3SeedLocation(bucket, key, fileKey, Region.EU_WEST_1);
    }
}
