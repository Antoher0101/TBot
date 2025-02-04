package com.mawus.core.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface LogService {

    Set<String> getLogFiles();

    List<String> getLogFileContent(String fileName);

    Stream<String> getLogFileContentAsStream(String fileName);
}
