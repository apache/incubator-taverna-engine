/**
 *
 */
package uk.org.taverna.platform.execution.impl.local;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.databundle.DataBundles;

/**
 * @author David Withers
 */
public class T2ReferenceConverter {
	public static Object convertPathToObject(Path path) throws IOException {
		Object object = null;
		if (DataBundles.isValue(path)) {
			object = DataBundles.getStringValue(path);
		} else if (DataBundles.isReference(path)) {
			URI reference = DataBundles.getReference(path);
			String scheme = reference.getScheme();
			if ("file".equals(scheme)) {
				object = new File(reference);
			} else {
				object = reference.toURL();
			}
		} else if (DataBundles.isList(path)) {
			List<Path> list = DataBundles.getList(path);
			List<Object> objectList = new ArrayList<Object>(list.size());
			for (Path pathElement : list) {
				objectList.add(convertPathToObject(pathElement));
			}
			object = objectList;
		}
		return object;
	}
}
