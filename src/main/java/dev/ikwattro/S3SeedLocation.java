package dev.ikwattro;

import software.amazon.awssdk.regions.Region;

public record S3SeedLocation(String bucket, String key, String fileKey, Region region) {}
