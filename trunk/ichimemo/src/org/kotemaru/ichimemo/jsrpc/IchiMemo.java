package org.kotemaru.ichimemo.jsrpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slim3.controller.upload.FileItem;
import org.slim3.datastore.*;

import org.kotemaru.ichimemo.model.*;
import org.kotemaru.ichimemo.meta.*;
import org.kotemaru.jsrpc.MultiPartMap;
import org.kotemaru.jsrpc.annotation.JsRpc;

import com.google.appengine.api.datastore.Key;

@JsRpc()
public class IchiMemo {
	
	public static String register(MultiPartMap params) {
		IchiMemoModel model = null;
		Long id = params.getLong("id");
		if (id != null) {
			Key key = Datastore.createKey(IchiMemoModel.class, id);
			model = Datastore.get(IchiMemoModel.class, key);
		} else {
			model = new IchiMemoModel();
		}

		List<Long> images = null;
		FileItem fileItem = params.getFileItem("file");
		if (fileItem != null && fileItem.getData().length != 0) {
			ImageModel img = new ImageModel();
			img.setContentType(fileItem.getContentType());
			img.setFileName(fileItem.getFileName());
			img.setData(fileItem.getData());
			Key imgKey = Datastore.put(img);
			images = new ArrayList<Long>(1);
			images.add(imgKey.getId());
		}
		
		model.setUsername(null); // TODO:
		model.setCreateDate(new Date());
		model.setUpdateDate(new Date());
		model.setLat(params.getDouble("lat"));
		model.setLng(params.getDouble("lng"));
		model.setAddress(params.getString("address"));
		model.setComment(params.getString("comment"));
		model.setLevel(params.getInteger("level"));
		model.setAppraise(params.getInteger("appraise"));
		model.setTags(Arrays.asList(params.getString("tags").split(",")));
		if (images != null) model.setImages(images);
		Key key = Datastore.put(model);

		return "Register "+key.getId();
	}

	public static ImageModel getImage(long id) {
		try {
			Key key = Datastore.createKey(ImageModel.class, id);
			ImageModel model = Datastore.get(ImageModel.class, key);
			return model;
		} catch (EntityNotFoundRuntimeException e) {
			return null;
		}
	}
	
	public static List<IchiMemoModel> list(Map params){
		double minLat = (Double) params.get("minLat");
		double minLng = (Double) params.get("minLng");
		double maxLat = (Double) params.get("maxLat");
		double maxLng = (Double) params.get("maxLng");
		String tag = (String) params.get("tag");

		IchiMemoModelMeta e = IchiMemoModelMeta.get();
		ModelQuery q = Datastore.query(e);
		if (tag != null) q.filter(e.tags.in(tag));
		q.filter(e.lat.greaterThan(minLat));

		List<IchiMemoModel> array = new ArrayList<IchiMemoModel>();
		Iterator<IchiMemoModel> ite = q.asIterator();
		while (ite.hasNext()) {
			IchiMemoModel model = ite.next();
			double lat = model.getLat();
			double lng = model.getLng();
			boolean inRange = (lat < maxLat && minLng < lng && lng < maxLng);
			if (inRange) {
				array.add(model);
			}
		}
		return array;
	}
}
