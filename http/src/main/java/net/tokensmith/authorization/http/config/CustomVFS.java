package net.tokensmith.authorization.http.config;

import org.apache.ibatis.io.VFS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomVFS extends VFS {
    private static String RESOURCE_PATTERN = "classpath*:%s/**/*.class";
    private final ResourcePatternResolver resourceResolver;

    public CustomVFS() {
        this.resourceResolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected List<String> list(URL url, String path) throws IOException {
        Resource[] resources = resourceResolver.getResources(String.format(RESOURCE_PATTERN, path));
        return Stream.of(resources)
                .map(resource -> preserveSubpackageName(resource, path))
                .collect(Collectors.toList());
    }

    private static String preserveSubpackageName(final Resource resource, final String rootPath) {
        try {
            String uriStr = resource.getURI().toString();
            int start = uriStr.indexOf(rootPath);
            return uriStr.substring(start);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
