package com.dxtr.vout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SingleListItem extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.single_list_view);
		TextView textProduct = (TextView)findViewById(R.id.product_label);
		Intent intent = getIntent();
		String product = intent.getStringExtra("product");
		textProduct.setText(product);
	}
}