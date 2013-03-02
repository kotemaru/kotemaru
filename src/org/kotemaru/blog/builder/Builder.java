package org.kotemaru.blog.builder;

import java.io.IOException;

public interface Builder  {

	public boolean build(BlogContext ctx) throws IOException;
	
}
