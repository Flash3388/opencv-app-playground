package project;

import com.castle.nio.PathMatching;
import com.castle.nio.PatternPathFinder;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Natives {

    public static final String OPENCV_LIBNAME = "opencv_java";

    public static void load(String libname) {
        try {
            loadFromClasspath(libname);
            return;
        } catch (Error e) {
        }
        loadFromFallbackFolder(libname);

        throw new AssertionError("Unable to load " + libname);
    }

    public static void loadFromClasspath(String libname) {
        String[] classpath = System.getProperty("java.class.path")
                .split(File.pathSeparator);
        for (String pathStr : classpath) {
            Path path = Paths.get(pathStr);
            if (!Files.isRegularFile(path)) {
                continue;
            }

            try {
                Zip zip = Zip.fromPath(path);
                loadFromZip(zip, libname);
                return;
            } catch (Error e) {
            }
        }
    }

    public static void loadFromZip(Zip zip, String libname) {
        try {
            Pattern pattern = compileSharedLibraryPattern(libname);

            try (OpenZip openZip = zip.open()) {
                Path pathInZip = openZip.find(pattern);
                extractFromZipAndLoad(openZip, pathInZip);
            }
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static void extractFromZipAndLoad(OpenZip openZip, Path path) throws IOException {
        Path extractPath;

        switch (com.castle.util.os.System.operatingSystem()) {
            case Windows: {
                extractPath = Paths.get(System.getProperty("user.dir"))
                        .resolve(path.getFileName().toString());
                if (!Files.exists(extractPath)) {
                    // not extracted
                    openZip.extractInto(path, extractPath);
                }
                break;
            }
            case Linux: {
                extractPath = openZip.extract(path).originalPath();
                break;
            }
            default:
                throw new AssertionError("unsupported platform");
        }

        System.load(extractPath.toAbsolutePath().toString());
    }

    public static void loadFromFallbackFolder(String libname) {
        try {
            Pattern pattern = compileSharedLibraryPattern(libname);

            String currendDir = System.getProperty("user.dir");
            Path fallbackFolder = Paths.get(currendDir, "natives");
            PatternPathFinder pathFinder = new PatternPathFinder(FileSystems.getDefault());
            Path jar = pathFinder.findOne(pattern, PathMatching.fileMatcher(), fallbackFolder);
            System.load(jar.toAbsolutePath().toString());
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private static Pattern compileSharedLibraryPattern(String libname) {
        String patternStr;
        switch (com.castle.util.os.System.operatingSystem()) {
            case Windows:
                patternStr = String.format("^.*%s\\d+\\.(?:dll)$", libname);
                break;
            case Linux:
                patternStr = String.format("^.*%s\\d+\\.(?:so)$", libname);
                break;
            default:
                throw new AssertionError("unsupported platform");
        }
        return Pattern.compile(patternStr);
    }
}
