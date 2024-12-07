package org.descartes.geoma.engine;

import java.util.Map;

public interface PathValidator {
    boolean validate(Map<String, String> tags);
}
