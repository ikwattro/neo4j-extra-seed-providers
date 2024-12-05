package dev.ikwattro;

import com.neo4j.dbms.seeding.SeedProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class S3ExtraSeedProvider extends SeedProvider {

    private static final String SCHEME = "aws-s3://";

    @Override
    public boolean matches(String uri) {
        return uri.startsWith(SCHEME);
    }

    @Override
    public Path download(String uri, Optional<String> credentials, Optional<String> config, Path path) {
        getLog().info("Attempting seed from `%s`".formatted(uri));
        var location = S3SeedUtils.locationFromURI(uri);
        try (var s3Client = buildClientWithDefaultCredentialsChain(location)) {

            File outFile = new File(path + "/%s".formatted(location.fileKey()));
            getLog().info("Storing s3 object into `%s`".formatted(outFile.toPath()));
            var getRequest = GetObjectRequest.builder().bucket(location.bucket()).key(location.key()).build();
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getRequest);
            try (FileOutputStream out = new FileOutputStream(outFile)) {
                out.write(response.readAllBytes());
            }

            return outFile.toPath();
        } catch (Exception e) {
            getLog().error("Failed to seed from uri `%s`".formatted(uri));
            throw new RuntimeException(e.getMessage());
        }
    }

    private S3Client buildClientWithDefaultCredentialsChain(S3SeedLocation location) {
        var builder = S3Client.builder().region(location.region());
        var witfEnv = System.getenv("AWS_CUSTOM_WEB_IDENTITY_TOKEN_FILE");
        var arn = System.getenv("AWS_ROLE_ARN");
        if (witfEnv != null && arn != null) {
            var provider = WebIdentityTokenFileCredentialsProvider.builder()
                            .roleArn(arn).webIdentityTokenFile(Paths.get("AWS_ROLE_ARN")).build();
            return builder.credentialsProvider(provider).build();
        }

        builder.credentialsProvider(AwsCredentialsProviderChain
                .builder()
                .addCredentialsProvider(DefaultCredentialsProvider.create())
                .reuseLastProviderEnabled(false)
                .build());

        return builder.build();
    }
}
