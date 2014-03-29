SmartPit
========

Android helper project. 


The MIT License (MIT)

Copyright (c) 2014 PiotrGryko

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


Import this library to your project to simplify building your app. SmartPit has a lot of code witch is common in most apps. 



To implements fragments based app make you fragments host activity extend SmartPitActivity.

....................

public class MainActivity extends SmartPitActivity {

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.setFirstFragment(fragmentSplash);
	}

}

.........................

SmartPitActivity implements SmartPitFragmentsInterface wich has usefull methods to switching fragments. 
Each fragments has to extends SmartPitFragment

To implement nested fragments with own backstack you can use default parent fragment  SmartPitBaseFragment class

                 ...

                SmartPitBaseFragment base = new SmartPitBaseFragment();  init BaseFragment
		base.setInitialFragment(new SmartPitBaseChildFragment()); add first child to base
                 
                ......

Now base contains initial firs SmartPitChildFragment. It acts the same like SmartPitActivity, 
each switchFragment(SmartPitBaseChildFragmnet, boolean removable) wil add new chill fragment on top of backstack. 

SmartPit library support volley images loading and DiskLruCache. On top of that custom ViewGroup SmartImageView 
will show loading circle while loading. 

To use volley based image loading, invoke: 
                
                ................

                SmartPitAppHelper.getInstance().setImage(Context context,SmartImageView imageView, String url,
				int reqWidth, reqHeight);
                ..............

You can create SmartImageView instance by new SmartImageView(Context context) , or by defining it 
in layout <com.example.widget.SmartImageView></>


 
