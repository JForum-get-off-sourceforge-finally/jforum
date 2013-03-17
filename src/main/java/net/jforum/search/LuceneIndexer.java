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
 * Created on 18/07/2007 17:18:41
 * 
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jforum.entities.Post;
import net.jforum.exceptions.SearchException;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * @author Rafael Steil
 * @version $Id$
 */
public class LuceneIndexer
{
	private static final Logger LOGGER = Logger.getLogger(LuceneIndexer.class);

	private LuceneSettings settings;
	private Directory ramDirectory;
	private IndexWriter ramWriter;
	private int ramNumDocs;
	private List<NewDocumentAdded> newDocumentAddedList = new ArrayList<NewDocumentAdded>();

	public LuceneIndexer(final LuceneSettings settings)
	{
		this.settings = settings;
		this.createRAMWriter();
	}

	public void watchNewDocuDocumentAdded(NewDocumentAdded newDoc)
	{
		this.newDocumentAddedList.add(newDoc);
	}

	public void batchCreate(final Post post)
	{
		synchronized (LOGGER) {
			try {
				final Document document = this.createDocument(post);
				this.ramWriter.addDocument(document);
				this.flushRAMDirectoryIfNecessary();
			}
			catch (IOException e) {
				throw new SearchException(e);
			}
		}
	}

	private void createRAMWriter()
	{
		try {
			if (this.ramWriter != null) {
				this.ramWriter.close();
			}

			this.ramDirectory = new RAMDirectory();
			final IndexWriterConfig conf = new IndexWriterConfig(LuceneSettings.version, this.settings.analyzer()).setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			this.ramWriter = new IndexWriter(this.ramDirectory, conf);
			this.ramNumDocs = SystemGlobals.getIntValue(ConfigKeys.LUCENE_INDEXER_RAM_NUMDOCS);
		}
		catch (IOException e) {
			throw new SearchException(e);
		}
	}

	private void flushRAMDirectoryIfNecessary()
	{
		if (this.ramWriter.maxDoc() >= this.ramNumDocs) {
			this.flushRAMDirectory();
		}
	}

	public void flushRAMDirectory()
	{
		synchronized (LOGGER) {
			IndexWriter writer = null;

			try {
				final IndexWriterConfig conf = new IndexWriterConfig(LuceneSettings.version, this.settings.analyzer()).setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
				writer = new IndexWriter(this.settings.directory(), conf);
				this.ramWriter.commit();
				this.ramWriter.close();
				writer.addIndexes(new Directory[] { this.ramDirectory });
				writer.forceMergeDeletes();

				this.createRAMWriter();
			}
			catch (IOException e) {
				throw new SearchException(e);
			}
			finally {
				if (writer != null) {
					try { 
						writer.commit(); 
						writer.close();

						this.notifyNewDocumentAdded();
					}
					catch (Exception e) {
						LOGGER.error(e.toString(), e);
					}
				}
			}
		}
	}

	public void create(final Post post)
	{
		synchronized (LOGGER) {
			IndexWriter writer = null;

			try {
				final IndexWriterConfig conf = new IndexWriterConfig(LuceneSettings.version, this.settings.analyzer()).setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
				writer = new IndexWriter(this.settings.directory(), conf);

				final Document document = this.createDocument(post);
				writer.addDocument(document);

				this.optimize(writer);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Indexed " + document);
				}
			}
			catch (Exception e) {
				LOGGER.error(e.toString(), e);
			}
			finally {
				if (writer != null) {
					try {
						writer.commit();
						writer.close();

						this.notifyNewDocumentAdded();
					}
					catch (Exception e) {
						LOGGER.error(e.toString(), e);
					}
				}
			}
		}
	}

	public void update(final Post post)
	{
		if (this.performDelete(post)) {
			this.create(post);
		}
	}

	private void optimize(final IndexWriter writer) throws Exception
	{
		if (writer.maxDoc() % 100 == 0) {
			LOGGER.info("Optimizing indexes. Current number of documents is " + writer.maxDoc());

			writer.forceMergeDeletes();

			LOGGER.debug("Indexes optimized");
		}
	}

	private Document createDocument(final Post post)
	{
		Document doc = new Document();

		doc.add(new Field(SearchFields.Keyword.POST_ID, String.valueOf(post.getId()), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field(SearchFields.Keyword.FORUM_ID, String.valueOf(post.getForumId()), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field(SearchFields.Keyword.TOPIC_ID, String.valueOf(post.getTopicId()), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field(SearchFields.Keyword.USER_ID, String.valueOf(post.getUserId()), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field(SearchFields.Keyword.DATE, this.settings.formatDateTime(post.getTime()), Store.YES, Index.NOT_ANALYZED));

		doc.add(new Field(SearchFields.Indexed.SUBJECT, post.getSubject(), Store.NO, Index.ANALYZED));
		doc.add(new Field(SearchFields.Indexed.CONTENTS, post.getText(), Store.NO, Index.ANALYZED));

		return doc;
	}

	private void notifyNewDocumentAdded()
	{
		for (Iterator<NewDocumentAdded> iter = this.newDocumentAddedList.iterator(); iter.hasNext(); ) {
			iter.next().newDocumentAdded();
		}
	}

	public void delete(final Post post)
	{
		this.performDelete(post);
	}

	private boolean performDelete(final Post post)
	{
		synchronized (LOGGER) {
			IndexWriter writer = null;
			boolean status = false;

			try {
				final IndexWriterConfig conf = new IndexWriterConfig(LuceneSettings.version, this.settings.analyzer()).setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
				writer = new IndexWriter(this.settings.directory(), conf);
				writer.deleteDocuments(new Term(SearchFields.Keyword.POST_ID, String.valueOf(post.getId())));
				status = true;
			}
			catch (IOException e) {
				LOGGER.error(e.toString(), e);
			}
			finally {
				if (writer != null) {
					try {
						writer.commit();
						writer.close();
						this.flushRAMDirectory();
					}
					catch (IOException e) {
						LOGGER.error(e.toString(), e);
					}
				}
			}

			return status;
		}
	}
}
