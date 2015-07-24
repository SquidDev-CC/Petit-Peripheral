package org.squiddev.petit.api.compile;

import javax.annotation.processing.ProcessingEnvironment;

public interface Environment extends ProcessingEnvironment {
	TypeHelpers getTypeHelpers();
}
