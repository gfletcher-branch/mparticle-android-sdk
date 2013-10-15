package com.mparticle.hello;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mparticle.MParticleAPI.EventType;
import com.mparticle.hello.musicplayer.MusicService;
import com.mparticle.hello.musicplayer.models.Playable;
import com.mparticle.hello.musicplayer.models.PlayableList;
import com.mparticle.hello.musicplayer.utils.ImageLoader;
import com.mparticle.hello.musicplayer.utils.Utils;

public class PerformanceActivity extends BaseActivity implements OnClickListener, OnKeyListener { 

	Button mStartStop;
	Button mSend10;
	Button mSend100;
	Button mSend500;
	Button mNext;

	boolean mRunning;

	Button mPlayButton;
	Button mPauseButton;
	Button mSkipButton;
	Button mRewindButton;
	Button mStopButton;
	Button mEjectButton;

	ListView mTrackListView;

	boolean mbFetchingList;

	PlayableList mPlayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_performance);

		mTrackListView = (ListView) findViewById(R.id.track_list);
		fetchPlayList(); // fetch the track list

		mPlayButton = (Button) findViewById(R.id.playbutton);
		mPauseButton = (Button) findViewById(R.id.pausebutton);
		mSkipButton = (Button) findViewById(R.id.skipbutton);
		mRewindButton = (Button) findViewById(R.id.rewindbutton);
		mStopButton = (Button) findViewById(R.id.stopbutton);
		mEjectButton = (Button) findViewById(R.id.ejectbutton);

		mPlayButton.setOnClickListener(this);
		mPauseButton.setOnClickListener(this);
		mSkipButton.setOnClickListener(this);
		mRewindButton.setOnClickListener(this);
		mStopButton.setOnClickListener(this);
		mEjectButton.setOnClickListener(this);

		mTrackListView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MusicService.ACTION_PLAY);
				Bundle extras = new Bundle();
				extras.putString("play_index", String.valueOf(position));
				intent.putExtras(extras);
				startService(intent);
			}

		});

		mStartStop = (Button)findViewById(R.id.btn_start_stop);
		mStartStop.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View vw) {
				if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) 
					mParticleAPI.logEvent("SDK Start/Stop Pressed", EventType.UserContent);
				if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) 
					mParticleAPI.logEvent("Play/Paused Pressed, is currently "+(mRunning?"":"not ")+"running", EventType.UserContent);
				mRunning = !mRunning;
				initializeMParticleAPI(); // make sure the api is initialized
				if (mRunning) {
					mStartStop.setText(R.string.btn_stop);
					smMParticleAPIEnabled = Boolean.valueOf(true);
				} else {
					mStartStop.setText(R.string.btn_start);
					smMParticleAPIEnabled = Boolean.valueOf(false);
				}
			}
		});
		mSend10 = (Button)findViewById(R.id.btn_send10);
		mSend10.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View vw) {
				if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) 
					mParticleAPI.logEvent("Send10 Pressed", EventType.UserContent);
				sendLog(10);
			}
		});
		mSend100 = (Button)findViewById(R.id.btn_send100);
		mSend100.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View vw) {
				if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) 
					mParticleAPI.logEvent("Send100 Pressed", EventType.UserContent);
				sendLog(100);
			}
		});
		mSend500 = (Button)findViewById(R.id.btn_send500);
		mSend500.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View vw) {
				if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) 
					mParticleAPI.logEvent("Send500 Pressed", EventType.UserContent);
				sendLog(500);
			}
		});
		mNext = (Button)findViewById(R.id.btn_next);
		mNext.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View vw) {
				if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) 
					mParticleAPI.logEvent("Next Pressed", EventType.UserContent);
				Intent intent = new Intent(PerformanceActivity.this, WebServiceActivity.class);
				startActivity(intent);
			}
		});
		mRunning = true;
		mStartStop.setText(R.string.btn_stop);
		
		startService(new Intent(MusicService.ACTION_STOP));
		
//		if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) 
//        	mParticleAPI.logScreenView("PerformanceActivity");
	}


	private void sendLog(int n) {
		if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
			for (int i=0; i<n; i++) {
				mParticleAPI.logEvent("AutoLog"+(i+1)+"Of"+n, EventType.UserContent);
			}
		}
	}


	public void onClick(View target) {
		// Send the correct intent to the MusicService, according to the button that was clicked
		if (target == mPlayButton) {
			if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
				mParticleAPI.logEvent("Play Pressed", EventType.UserContent);
				startService(new Intent(MusicService.ACTION_PLAY));
			}	
		}
		else if (target == mPauseButton) {
			if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
				mParticleAPI.logEvent("Play Pressed", EventType.UserContent);
			}
			startService(new Intent(MusicService.ACTION_PAUSE));
		}	
		else if (target == mSkipButton) {
			if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
				mParticleAPI.logEvent("Skip Pressed", EventType.UserContent);
			}
			startService(new Intent(MusicService.ACTION_SKIP));
		}	
		else if (target == mRewindButton) {
			if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
				mParticleAPI.logEvent("Rewind Pressed", EventType.UserContent);
			}
			startService(new Intent(MusicService.ACTION_REWIND));
		}	
		else if (target == mStopButton) {
			if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
				mParticleAPI.logEvent("Stop Pressed", EventType.UserContent);
			}
			startService(new Intent(MusicService.ACTION_STOP));
		}	
		else if (target == mEjectButton) {
			if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
				mParticleAPI.logEvent("Eject Pressed", EventType.UserContent);
			}	
			showUrlDialog();
		}
	}

	/** 
	 * Shows an alert dialog where the user can input a URL. After showing the dialog, if the user
	 * confirms, sends the appropriate intent to the {@link MusicService} to cause that URL to be
	 * played.
	 */
	void showUrlDialog() {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(getString(R.string.manual_entry));
		alertBuilder.setMessage(getString(R.string.enter_url));
		final EditText input = new EditText(this);
		alertBuilder.setView(input);

		input.setText(getString(R.string.default_music_url));

		alertBuilder.setPositiveButton(getString(R.string.choose_play), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dlg, int whichButton) {
				if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
					mParticleAPI.logEvent("Play from URL Dialog Pressed", EventType.UserContent);
				}
				// Send an intent with the URL of the song to play. This is expected by
				// MusicService.
				Intent i = new Intent(MusicService.ACTION_URL);
				Uri uri = Uri.parse(input.getText().toString());
				i.setData(uri);
				startService(i);
			}
		});
		alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dlg, int whichButton) {
				if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
					mParticleAPI.logEvent("Cancel from URL Dialog Pressed", EventType.UserContent);
				}
			}
		});

		alertBuilder.show();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() != KeyEvent.ACTION_DOWN) { 
			return false;
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
		case KeyEvent.KEYCODE_HEADSETHOOK:
			startService(new Intent(MusicService.ACTION_TOGGLE_PLAYBACK));
			return true;
		}
		return false;
	}

	// asynch fetch and update of the playlist
	private void fetchPlayList() {
		if (mbFetchingList) return;
		mbFetchingList = true;
		// poll for results
		Thread t = new Thread() {
			@Override
			public void run() {
				while (true) {
					if (MusicService.smService == null) {
						startService(new Intent(MusicService.ACTION_STOP));
					} else {
						mPlayList = MusicService.smService.getPlaylist();
						if (mPlayList != null) {
							mbFetchingList = false;
							Runnable r = new Runnable() {
								@Override 
								public void run() {
									// update the listview
									mTrackListView.setAdapter(new TrackListAdapter(PerformanceActivity.this, mPlayList));
									mTrackListView.requestLayout();
									findViewById(R.id.empty_media).setVisibility(View.GONE);
									if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
										mParticleAPI.logEvent("ListView updated", EventType.UserContent);
									}
								}
							};
							runOnUiThread(r);
							return;
						}
					}
					try {
						new Thread().sleep(1000L);
					} catch(InterruptedException e) {
					}
				}
			}
		};
		t.start();
	}

	class TrackListAdapter extends BaseAdapter {

		private Activity activity;
		private PlayableList data;
		private LayoutInflater inflater=null;
		public ImageLoader imageLoader; 

		public TrackListAdapter(Activity a, PlayableList pl) {
			activity = a;
			data=pl;
			inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageLoader=new ImageLoader(activity.getApplicationContext());
		}

		public int getCount() {
			if (data == null) return 0;
			return data.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View vi=convertView;
			if(convertView==null)
				vi = inflater.inflate(R.layout.song_list_item, null);

			TextView title = (TextView)vi.findViewById(R.id.title); // title
			TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
			TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
			ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

			if (data != null) {
				Playable song = data.get(position);

				// Setting all values in listview
				title.setText(song.getTitle());
				artist.setText(song.getArtist());
				duration.setText(Utils.formatSongDuration(song.getDuration()));

				if ((song.getImageUrl() != null) && (song.getImageUrl().length() > 0)) {
					imageLoader.DisplayImage(song.getImageUrl(), thumb_image);
				}
				if ((mParticleAPI != null) && (smMParticleAPIEnabled != null) && smMParticleAPIEnabled) {
					mParticleAPI.logEvent("ListItem: "+song.getTitle()+" by "+song.getArtist()+" updated", EventType.UserContent);
				}
			}
			return vi;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		startService(new Intent(MusicService.ACTION_STOP));
	}

	@Override
	protected void onStop() {
		super.onStop();
		startService(new Intent(MusicService.ACTION_STOP));
	}

}