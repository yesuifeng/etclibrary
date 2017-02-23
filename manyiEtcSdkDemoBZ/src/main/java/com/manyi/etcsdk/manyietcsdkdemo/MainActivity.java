package com.manyi.etcsdk.manyietcsdkdemo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.manyi.mobile.activity.OpenCardEtcActivity;
import com.manyi.mobile.entiy.EtcCardParam;
import com.manyi.mobile.etcsdk.activity.EtcTranferHomeActivity;
import com.manyi.mobile.etcsdk.activity.GSETC;
import com.manyi.mobile.etcsdk.activity.ReadETCBlueTooth;
import com.manyi.mobile.etcsdk.activity.ReadEtcNFC;
import com.manyi.mobile.etcsdk.activity.ReadEtcUSB;
import com.manyi.mobile.etcsdk.entity.AuthParam;
import com.manyi.mobile.etcsdk.interfaces.MYRequestCallBack;
import com.manyi.mobile.utils.Common;
import com.manyi.mobile.utils.Constants;
import com.manyi.mobile.widget.CustomDialog;
import com.xinlian.cardsdk.CardSDK;

public class MainActivity extends Activity {
	private TextView txtResult;
	private static final int REQUEST_ETC = 0x1;
	private static final int REQUEST_RECHARGE = 0x3;
	private static final int REQUEST_OPENCARD = 0x4;
	private TextView editText1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_activity);
		TextView txtVersion = (TextView) findViewById(R.id.txtVersion);
		txtVersion.setText(GSETC.BUILD_TIME);
		txtResult = (TextView) findViewById(R.id.txtResult);
		/**
		 * 调用这句话，使用测试环境
		 * 不调用或者GSETC.getInstance(this).setDebug(false)为生产环境
		 */
//		GSETC.getInstance(this).setDebug(true);
		GSETC.getInstance(this).iniGSETC(this,
				new AuthParam("4556636767", "18654517708", "010174"));
		// GSETC.getInstance(this).iniGSETC(this,
		// new AuthParam("满易网提供的key", "用户手机号"));
		// "8976505124", "18654517705"
		// "满易网提供的key", "用户手机号"
		GSETC.getInstance(this).checkApp(this, null, new MYRequestCallBack() {

			@Override
			public void onSuccess(String resp) {
				// TODO Auto-generated method stub

			} 

			@Override
			public void onFailed(String resp) {
				// TODO Auto-generated method stub

			}
		});
		editText1 = (EditText) findViewById(R.id.editText1);

	}

	/**
	 * 读卡功能
	 * 
	 * @param v
	 */
	public void readCardOnline(View v) {// 阻塞主线程，这里建议用线程实现
		txtResult.setText("");
		if (editText1.length() > 0)
			GSETC.getInstance(MainActivity.this).getEtcInfo(MainActivity.this,
					editText1.getText().toString(), new MYRequestCallBack() {

						@Override
						public void onSuccess(String result) {
							// TODO Auto-generated method stub
							try {
								JSONObject json = new JSONObject(result);
								txtResult
										.setText(editText1.getText().toString()
												+ "读卡成功:"
												+ json.getJSONObject("data")
														.toString());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						@Override
						public void onFailed(String result) {
							// TODO Auto-generated method stub
							txtResult.setText(result);
						}
					});
	}

	public void readCard(View v) {
		startActivity(new Intent(this, ReadETCBlueTooth.class).putExtra(
				"isRead", true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

	/**
	 * 写卡功能
	 * 
	 * @param v
	 */
	public void ndcEtcQc(View v) {
		gotoNFC();
	}

	public void myETCList(View v) {
		startActivityForResult(new Intent(this,
				com.manyi.mobile.etcsdk.activity.ETCList.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), REQUEST_ETC);
	}

	/**
	 * 写卡功能
	 * 
	 * @param v
	 */
	public void mposEtcQc(View v) {
		startActivity(new Intent(this, ReadETCBlueTooth.class).putExtra(
				"isRead", false).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

	/**
	 * USB写卡功能
	 * 
	 * @param v
	 */
	public void usbEtcQc(View v) {
		startActivity(new Intent(this, ReadEtcUSB.class).putExtra("isRead",
				false).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

	}

	/**
	 * 充值功能
	 * 
	 * @param v
	 */
	public void recharge(View v) {
		Intent intent = new Intent(this, EtcTranferHomeActivity.class)
				.putExtra("noCardCharge", 1).putExtra("position", 0)
				.putExtra("packageName", "com.manyi.mobile.etc")
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/**
	 * 我的订单
	 * 
	 * @param v
	 */
	public void myOrder(View v) {
		Intent intent = new Intent(this,
				com.manyi.mobile.etcsdk.activity.MyListView.class)
				.putExtra("style", Constants.ORDERLIST)
				.putExtra("state", "pending")
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, REQUEST_ETC);
	}

	/**
	 * 开卡申请
	 * 
	 * @param v
	 */
	public void openCard(View v) {
		//可以将开卡用户的已知数据通过EtcCardParam对象传过去
		Intent intent = new Intent(this, OpenCardEtcActivity.class);
		EtcCardParam etcCardParam = new EtcCardParam(
				"张三",
				1,
				"12345678900123",
				"http://image.tf56.com/dfs/group2//M00//A8//9C//123.png",
				"http://image.tf56.com/dfs/group2//M00//A8//9C//121.png",
				"http://image.tf56.com/dfs/group2//M00//A8//9C//122.png",
				"http://image.tf56.com/dfs/group2//M00//A8//9C//121.png",
				"", "18200000000", "鲁A1234", "0", "0", "想念你想", "11", 10,
				"1");
		Bundle bundle = new Bundle();
		bundle.putSerializable("etcCardParam", etcCardParam);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	/**
	 * NFC
	 */
	private void gotoNFC() {
		final int ret = CardSDK.instance().getNFCStatus(this);
		if (ret == -1) {
			Common.showToast(this, "对不起，因为您的手机不支持NFC功能，所以不能使用手机写卡功能！");
		} else if (ret == 0) {
			final CustomDialog dialognfc = new CustomDialog(this);
			dialognfc.setDisMiss(false);
			dialognfc.setlinecolor();
			dialognfc.setTitle("提示");
			dialognfc.setContentboolean(true);
			dialognfc.setDetial("NFC未打开，请先打开NFC功能");
			dialognfc.setLeftText("确认");
			dialognfc.setRightText("取消");
			dialognfc.setLeftOnClick(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialognfc.dismiss();
					startActivity(new Intent("android.settings.NFC_SETTINGS"));
				}
			});
			dialognfc.setRightOnClick(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialognfc.dismiss();
				}
			});
			dialognfc.show();
		} else {
			startActivity(new Intent(this, ReadEtcNFC.class).putExtra("isRead",
					false).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		}
	}
}
