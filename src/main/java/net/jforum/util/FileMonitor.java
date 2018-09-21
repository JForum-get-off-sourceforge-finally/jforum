/*
 * Copyright (c) 2003, 2004 Rafael Steil
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
 * Created on 02/06/2004 23:29:51
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Monitor class for file changes.
 * 
 * @author Rafael Steil
 */

public class FileMonitor
{
    private static final Logger LOGGER = Logger.getLogger(FileMonitor.class);
    private static final FileMonitor INSTANCE = new FileMonitor();
    private Map<String, FileAlterationMonitor> timerEntries;

    private FileMonitor() {
        this.timerEntries = new ConcurrentHashMap<String, FileAlterationMonitor>();
    }

    public static FileMonitor getInstance() {
        return INSTANCE;
    }

    /**
     * Add a file to the monitor
     * 
     * @param listener The file listener
     * @param filename The filename to watch
     * @param period The watch interval (in milli seconds)
     */
    public void addFileChangeListener (final FileChangeListener listener, final String filename, final long period) {
		final String absoluteFilename = new File(filename).getAbsolutePath();
        this.removeFileChangeListener(absoluteFilename);
       	LOGGER.info("Watching " + absoluteFilename);

		FileAlterationObserver observer = new FileAlterationObserver(new File(filename).getParent());
		observer.addListener(new FileAlterationListenerAdaptor() {
			@Override
			public void onFileChange (File file) {
                String absPath = file.getAbsolutePath();
				if (absPath.equals(absoluteFilename)) {
					//System.out.println("File changed: " + absoluteFilename);
					listener.fileChanged(absPath);
				}
			}
		});

		FileAlterationMonitor monitor = new FileAlterationMonitor(period, observer);
		try {
			monitor.start();
			this.timerEntries.put(filename, monitor);
		} catch (Exception ex) {
        	LOGGER.error("Error watching " + filename + ": " + ex.getMessage());
		}
    }

    /**
     * Stop watching a file
     * 
     * @param filename The filename to keep watch
     */
    public void removeFileChangeListener (final String filename) {
		final String absoluteFilename = new File(filename).getAbsolutePath();
        FileAlterationMonitor monitor = this.timerEntries.remove(absoluteFilename);
        if (monitor != null) {
			try {
				monitor.stop();
			} catch (Exception ex) {
				LOGGER.error("Error unwatching " + absoluteFilename + ": " + ex.getMessage());
			}
        }
    }
}
