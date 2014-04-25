package org.kotemaru.android.gmaptest;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {
	private GoogleMap gMap;
	private LocationClient locationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded(); // サンプルに従っているがここでも呼ぶ意味は良く分からない。
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocation(true);
	}

	// ---------------------------------------------------------------------
	// マップ初期化処理
	private void setUpMapIfNeeded() {
		if (gMap == null) {
			gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			if (gMap != null) {
				setUpMap();
			}
		}
	}
	private void setUpMap() {
		// 初期座標、拡大率設定
		LatLng latLng = new LatLng(35.684699, 139.753897);
		float zoom = 13; // 2.0～21.0
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

		// マーカー設置
		gMap.addMarker(new MarkerOptions().position(latLng).title("皇居"));
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {
				// この marker は保存するとリークするよ。
				String msg = "Marker onClick:" + marker.getTitle();
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
				return false;
			}
		});
	}

	// --------------------------------------------------------------------------------
	// 以下、GPS連動の設定。
	private void setUpLocation(boolean isManual) {
		if (isManual) {
			// 画面右上にGPSボタンが表示される。
			// タップすると現在地への移動までかってにやってくれる。
			gMap.setMyLocationEnabled(true);
		} else {
			// 現在地を定期的に取得する設定。
			if (locationClient == null) {
				locationClient = new LocationClient(
						getApplicationContext(),
						connectionCallbacks,
						onConnectionFailedListener);
				locationClient.connect();
			}
		}
	}

	ConnectionCallbacks connectionCallbacks = new ConnectionCallbacks() {
		private final LocationRequest locationRequest = LocationRequest.create()
				.setInterval(5000)         // 5 seconds
				.setFastestInterval(5000)
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		@Override
		public void onConnected(Bundle arg0) {
			locationClient.requestLocationUpdates(locationRequest, locationListener);
		}
		@Override
		public void onDisconnected() {
			// nop.
		}
	};
	OnConnectionFailedListener onConnectionFailedListener = new OnConnectionFailedListener() {
		@Override
		public void onConnectionFailed(ConnectionResult connectionResult) {
			// nop.;
		}
	};
	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			float zoom = 20;
			gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
		}
	};

}
