package com.axmexa.gxtapp.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Icons extends ClientBundle {

	
	Icons INSTANCE = GWT.create(Icons.class);
	
	@Source("leaf.png")
	ImageResource getLeafImage();
	
	@Source("fromdb.png")
	ImageResource getFromDBImage();
	
	@Source("todb.png")
	ImageResource getToDBImage();
	
	@Source("fromfile.png")
	ImageResource getFromFileImage();
	
	@Source("new.png")
	ImageResource getNewImage();
	
	@Source("toleft.png")
	ImageResource gettoleftImage();
	
	@Source("toright.png")
	ImageResource gettorightImage();

    @Source("allleft.png")
    ImageResource getallleftImage();
}
