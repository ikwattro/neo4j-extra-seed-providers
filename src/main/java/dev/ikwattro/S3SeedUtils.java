package dev.ikwattro;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class S3SeedUtils {

    private static final String SCHEME = "aws-s3://";

    public static S3SeedLocation locationFromURI(String uri) {
        var reduced = uri.replace(SCHEME, "");
        var parts = Arrays.asList(reduced.split("/"));
        var bucket = parts.get(0);
        var fileKey = parts.get(parts.size()-1);
        var key = StringUtils.join(parts.subList(1, parts.size()), "/");

        if (!key.endsWith(".backup") && !key.endsWith(".dump")) {
            throw new IllegalArgumentException("Invalid backup key : %s. It must end with `.backup` or `.dump`".formatted(key));
        }

        return new S3SeedLocation(bucket, key, fileKey);
    }
}
