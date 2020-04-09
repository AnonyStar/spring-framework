/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.io;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 从给定的 class 下加载资源
 * {@link ResourceLoader} implementation that interprets plain resource paths
 * as relative to a given {@code java.lang.Class}.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see Class#getResource(String)
 * @see ClassPathResource#ClassPathResource(String, Class)
 */
public class ClassRelativeResourceLoader extends DefaultResourceLoader {

	private final Class<?> clazz;


	/**
	 * Create a new ClassRelativeResourceLoader for the given class.
	 * @param clazz the class to load resources through
	 */
	public ClassRelativeResourceLoader(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		this.clazz = clazz;
		setClassLoader(clazz.getClassLoader());
	}

	/**
	 * 重写getResourceByPath 方法 ， 返回一个ClassRelativeContextResource 资源类型
	 * @param path the path to the resource
	 * @return
	 */
	@Override
	protected Resource getResourceByPath(String path) {
		return new ClassRelativeContextResource(path, this.clazz);
	}


	/**
	 * 继承 ClassPathResource  定义资源类型，实现ContextResource 中的 getPathWithinContext 方法，
	 *
	 * ClassPathResource that explicitly expresses a context-relative path
	 * through implementing the ContextResource interface.
	 */
	private static class ClassRelativeContextResource extends ClassPathResource implements ContextResource {

		private final Class<?> clazz;

		/**
		 * 调用父类 ClassPathResource 对资源进行初始化
		 * @param path
		 * @param clazz
		 */
		public ClassRelativeContextResource(String path, Class<?> clazz) {
			super(path, clazz);
			this.clazz = clazz;
		}

		@Override
		public String getPathWithinContext() {
			return getPath();
		}

		/**
		 * 重写 ClassPathContext 中方法， 通过给定的路径返回一个ClassRelativeContextResource资源
		 * @param relativePath the relative path (relative to this resource)
		 * @return
		 */
		@Override
		public Resource createRelative(String relativePath) {
			String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
			return new ClassRelativeContextResource(pathToUse, this.clazz);
		}
	}

}
