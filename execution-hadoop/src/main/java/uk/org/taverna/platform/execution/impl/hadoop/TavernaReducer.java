package uk.org.taverna.platform.execution.impl.hadoop;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.fs.Path;

public class TavernaReducer extends
		org.apache.hadoop.mapreduce.Reducer<int[], Map<String, Path>, Object, Object> {

	private Context context;

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		this.context = context;
	}

	@Override
	protected void reduce(int[] key, Iterable<Map<String, Path>> values,
			Context context) throws IOException, InterruptedException {
	}

}
