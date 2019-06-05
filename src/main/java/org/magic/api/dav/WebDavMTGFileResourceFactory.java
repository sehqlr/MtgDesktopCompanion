package org.magic.api.dav;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.magic.api.fs.MTGFileSystem;
import org.magic.api.fs.MTGPath;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

import io.milton.common.Path;
import io.milton.http.ResourceFactory;
import io.milton.resource.Resource;

public class WebDavMTGFileResourceFactory implements ResourceFactory
{
	private MTGFileSystem fs;
	protected Logger log = MTGLogger.getLogger(this.getClass());

    
	public WebDavMTGFileResourceFactory() throws SQLException {
		MTGDao dao = MTGControler.getInstance().getEnabled(MTGDao.class);
		dao.init();
		fs = new MTGFileSystem(dao);
	}
	
	
	
    @Override
    public Resource getResource(String host, String url) {
        log.debug("getResource: host: " + host + " - url:" + url);
        
        Path ioPath = Path.path(url);
        MTGPath mtgpath = (MTGPath) fs.getPath(ioPath.toPath());
        
        if(mtgpath.isCard())
        	return new MTGDavFileResource(mtgpath,fs,ioPath.isRoot());
        else
        	return new MTGDavFolderResource(mtgpath,fs,ioPath.isRoot());

     }
    
    
	
}