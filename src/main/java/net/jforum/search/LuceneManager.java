/*
 * Copyright (c) JForum Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer.
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * Created on 24/07/2007 12:23:05
 * 
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.search;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Date;

import net.jforum.entities.Post;
import net.jforum.exceptions.ForumException;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.IndexUpgrader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.util.InfoStream;

import org.apache.commons.io.FileUtils;

import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 */

public class LuceneManager 
{
	private static final Logger LOGGER = Logger.getLogger(LuceneManager.class);

	private LuceneSearch search;
	private LuceneSettings settings;
	private LuceneIndexer indexer;

	public void init()
	{
		String dirPath = SystemGlobals.getValue(ConfigKeys.LUCENE_INDEX_WRITE_PATH);

		try {
			Class<?> clazz = Class.forName(SystemGlobals.getValue(ConfigKeys.LUCENE_ANALYZER));

			settings = new LuceneSettings(clazz);
			settings.useFSDirectory(dirPath);

			boolean reindex = false;
			try {
				DirectoryReader.open(settings.directory());
			} catch (IndexFormatTooOldException iftoex) {
				LOGGER.warn("Search index format too old - reindexing all posts, which can take a while");

				FileUtils.cleanDirectory(new File(dirPath));
				settings.useFSDirectory(dirPath);

				// create an empty directoy
				settings.createIndexDirectory(dirPath);

				reindex = true;
			}

			indexer = new LuceneIndexer(settings);

			search = new LuceneSearch(settings, new LuceneContentCollector(settings));

			indexer.watchNewDocuDocumentAdded(search);

			SystemGlobals.setObjectValue(ConfigKeys.LUCENE_SETTINGS, settings);

			// reindex everything - from Jan 1 1970 to now
			if (reindex) {
				LuceneReindexArgs args = new LuceneReindexArgs(new Date(70, 0, 1), new Date(),
											0, 0, false, LuceneReindexArgs.TYPE_DATE, true);
				LuceneReindexer reindexer = new LuceneReindexer(settings, args);
				reindexer.startBackgroundProcess();
			}
		} catch (Exception ex) {
			throw new ForumException(ex);
		}
	}

	public LuceneSearch luceneSearch()
	{
		return search;
	}

	public LuceneIndexer luceneIndexer()
	{
		return indexer;
	}

	public void create(final Post post)
	{
		indexer.create(post);
	}

	public void update(final Post post)
	{
		indexer.update(post);
	}

	public SearchResult<Post> search(final SearchArgs args, int userId)
	{
		return search.search(args, userId);
	}

	public void delete(final Post post)
	{
		indexer.delete(post);
	}
}
