package edu.temple.stockquote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	EditText enterStocks;
	Button   searchStocks;
	TextView viewStockPrice;
	TextView viewStockName;
	
	String stockSymbol;
	
	String stockPrice;
	String stockName;
	
	// the handler that gets the data from the thread and the displays the data
		final Handler getContentHandler = new Handler(){
			
			@Override
			public void handleMessage(Message msg) {
				try {
					
					JSONObject mainJsonObject = new JSONObject(msg.obj.toString());
					
				
					JSONObject listJsonObject = mainJsonObject.getJSONObject("list");
					
					JSONArray jsonArray = listJsonObject.getJSONArray("resources");
					
					JSONObject content = jsonArray.getJSONObject(0);
					
					JSONObject jsonFields = content.getJSONObject("fields");
					
					stockPrice = jsonFields.getString("price");
					stockName = jsonFields.getString("symbol");
					
					viewStockName.setText(stockName);
					viewStockPrice.setText("stockPrice");
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		enterStocks = (EditText) findViewById(R.id.editText_EnterStocks);
		viewStockName = (TextView) findViewById(R.id.textView_ViewStock_Name);
		viewStockPrice = (TextView) findViewById(R.id.textView_price);
		searchStocks = (Button) findViewById(R.id.button_SearchStocks);
		
		searchStocks.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
					
					Thread getURL = new Thread(){
						
					    @Override
						public void run(){
					
							if (isNetworkActive()){
								
								String urlString = "http://finance.yahoo.com/webservice/v1/symbols/" + stockSymbol + "/quote?format=json";
								URL url = null;
								
								try {
									url = new URL(urlString);
									BufferedReader reader = new BufferedReader(
											new InputStreamReader(
													url.openStream()));
									
									String response = "", tmpResponse = "";
									
									tmpResponse = reader.readLine();
									while (tmpResponse != null){
										response = response + tmpResponse;
										tmpResponse = reader.readLine();
									}
									
									Message msg = getContentHandler.obtainMessage();
									
								
									msg.obj = response;
									
									getContentHandler.sendMessage(msg);
									
								} catch (Exception e) {
									e.printStackTrace();
								}
								
							}
						}
					};
					
					getURL.start();
					
						
					}
				});
		    }
		    // Method that does the connection   
		    public boolean isNetworkActive(){
		    		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		    		if (networkInfo != null && networkInfo.isConnected()) {
		    			return true;
		    		} else {
		    			return false;
		    		}
		    }
		}
