/*******************************************************************************
 * Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 * Copyright (c) 2011- kotemaru@kotemaru.org
 ******************************************************************************/
package org.kotemaru.util.jsg;

import java.io.* ;
import java.util.* ;
import java.lang.reflect.* ;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;


public interface Tokenizer {
	public Token tokenize(BnfDriver driver);
}

