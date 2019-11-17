package me.piggypiglet.framework.nukkit.file;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import com.google.common.io.Files;
import me.piggypiglet.framework.file.framework.AbstractFileConfiguration;
import me.piggypiglet.framework.file.framework.FileConfiguration;
import me.piggypiglet.framework.utils.StringUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NukkitFileConfiguration extends AbstractFileConfiguration {
    private Config config;

    public NukkitFileConfiguration() {
        super(s -> StringUtils.anyEndWith(s, ".properties", ".con", ".conf", ".config", ".yml", ".yaml", ".txt", ".list", ".enum"));
    }

    @SuppressWarnings("UnstableApiUsage")
    private NukkitFileConfiguration(Map<String, Object> map) {
        this();

        config = new Config(
                getFile(),
                Config.format.get(Files.getFileExtension(getFile().getName())),
                new ConfigSection((LinkedHashMap<String, Object>) map)
        );
    }

    @Override
    protected void internalLoad(File file, String fileContent) {
        config = new Config(file);
    }

    @Override
    protected Map<String, Object> retrieveAll() {
        return config.getAll();
    }

    @Override
    public Object get(String path) {
        return config.get(path);
    }

    @Override
    public FileConfiguration getConfigSection(String path) {
        ConfigSection section = config.getSection(path);

        if (section != null) {
            return sectionToFileConfiguration(section);
        }

        return null;
    }

    @Override
    public String getString(String path) {
        return config.getString(path);
    }

    @Override
    public Integer getInt(String path) {
        return config.getInt(path);
    }

    @Override
    public Long getLong(String path) {
        return config.getLong(path);
    }

    @Override
    public Double getDouble(String path) {
        return config.getDouble(path);
    }

    @Override
    public Boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    @Override
    public List<FileConfiguration> getConfigList(String path) {
        return null;
    }

    @Override
    public List<?> getList(String path) {
        return config.getList(path);
    }

    private FileConfiguration sectionToFileConfiguration(ConfigSection section) {
        return new NukkitFileConfiguration(section.getAllMap());
    }
}
