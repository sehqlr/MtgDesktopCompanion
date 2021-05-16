package org.beta;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.services.MTGConstants;
import org.magic.tools.ImageTools;


public class SlightlyMagicPictureProvider extends AbstractPicturesProvider {

	private static final String PICS_DIR = "PICS_DIR";

	
	@Override
	public String getName() {
		return "Slightlymagic";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}


	@Override
	public BufferedImage extractPicture(MagicCard mc) throws IOException {
		return null;
	}

	
	@Override
	public BufferedImage getPicture(MagicCard mc) throws IOException {
		return getOnlinePicture(mc);
	}
	
		public static void main(String[] args) {
			FileUtils.listFiles(new File("C:\\Users\\Nicolas\\.magicDeskCompanion\\data\\forge\\IKO"), new WildcardFileFilter("Colossification*"),TrueFileFilter.INSTANCE).forEach(System.out::println);;
			
		}
	
	
	@Override
	public String generateUrl(MagicCard mc) {
		
		if(!getFile(PICS_DIR).exists())
			try {
				FileUtils.forceMkdir(getFile(PICS_DIR));
			} catch (IOException e) {
				logger.error("Couldn't create " + getString(PICS_DIR) + " directory");
			}
			
		
		var edDir = new File(getFile(PICS_DIR),mc.getCurrentSet().getId());
		int size = FileUtils.listFiles(edDir, new WildcardFileFilter(mc.getName()+"*"),TrueFileFilter.INSTANCE).size();
		
		String calculate = "";
		
		
		
		return new File(edDir, mc.getName() + calculate +".fullborder.jpg").getAbsolutePath();
	}
	
	@Override
	public BufferedImage getOnlinePicture(MagicCard mc) throws IOException {
	
		try {
			return ImageTools.read(new File(generateUrl(mc)));
		}
		catch(Exception e)
		{
			logger.debug(generateUrl(mc) + " is not found");
			return null;
		}
	}
	
	@Override
	public void initDefault() {
		setProperty(PICS_DIR,Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(),"forge").toFile().getAbsolutePath());
	}

	
}
