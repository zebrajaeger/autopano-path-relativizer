package de.zebrajaeger.autopanaopathrelativizer;

import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@AllArgsConstructor
public class FileNameTransformer {
    public static final FileNameTransformer DEFAULT = new FileNameTransformer("%d%s%n.relative.%e");
    private final String template;

    public  String transform(File file){
        return template
                .replace("%d", file.getParent())
                .replace("%s", File.separator)
                .replace("%n", FilenameUtils.getBaseName(file.getName()))
                .replace("%e", FilenameUtils.getExtension(file.getName()));
    }
}
