package dev.ikwattro;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

import static org.assertj.core.api.Assertions.*;

public class ParseUrlTest {

    @Test
    void parseUrl() {
        var uri = "aws-s3://datasets/graphs/folder/Chattanooga-5.5.0-2023-03-13T11-38-02.backup";
        var location = S3SeedUtils.locationFromURI(uri);
        assertThat(location.key()).isEqualTo("graphs/folder/Chattanooga-5.5.0-2023-03-13T11-38-02.backup");
        assertThat(location.bucket()).isEqualTo("datasets");
        assertThat(location.fileKey()).isEqualTo("Chattanooga-5.5.0-2023-03-13T11-38-02.backup");
    }

    @Test
    void location_test() {
        var uri = "aws-s3://neo4j.tests.buckets.io/backups/neo4j5-cluster/latest/goldkg-latest.backup";
        var location = S3SeedUtils.locationFromURI(uri);
        assertThat(location.key()).isEqualTo("backups/neo4j5-cluster/latest/goldkg-latest.backup");
        assertThat(location.bucket()).isEqualTo("neo4j.tests.buckets.io");
        assertThat(location.fileKey()).isEqualTo("goldkg-latest.backup");
    }

    @Test
    void default_region() {
        var uri = "aws-s3://neo4j.tests.buckets.io/backups/neo4j5-cluster/latest/goldkg-latest.backup";
        var location = S3SeedUtils.locationFromURI(uri);
        assertThat(location.key()).isEqualTo("backups/neo4j5-cluster/latest/goldkg-latest.backup");
        assertThat(location.bucket()).isEqualTo("neo4j.tests.buckets.io");
        assertThat(location.fileKey()).isEqualTo("goldkg-latest.backup");
        assertThat(location.region()).isEqualTo(Region.EU_WEST_1);
    }

    @Test
    void region_in_url_test() {
        var uri = "aws-s3://neo4j.tests.buckets.io/backups/neo4j5-cluster/latest/goldkg-latest.backup?region=ap-southeast-2";
        var location = S3SeedUtils.locationFromURI(uri);
        assertThat(location.key()).isEqualTo("backups/neo4j5-cluster/latest/goldkg-latest.backup");
        assertThat(location.bucket()).isEqualTo("neo4j.tests.buckets.io");
        assertThat(location.fileKey()).isEqualTo("goldkg-latest.backup");
        assertThat(location.region()).isEqualTo(Region.AP_SOUTHEAST_2);
    }
}
