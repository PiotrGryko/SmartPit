package pl.gryko.smartpitlib.widget;


import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

public class SmartPitTabContent implements TabContentFactory {
	private Context context;

	public SmartPitTabContent(Context context) {
		this.context = context;
	}

	@Override
	public View createTabContent(String tag) {
		return new View(context);

	}

}
