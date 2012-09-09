package com.kevin.ringtone50.adapter;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.kevin.ringtone50.R;
import com.kevin.ringtone50.activity.MainActivity;
import com.kevin.ringtone50.data_model.MyRingtone;
import com.kevin.ringtone50.media.RingtonePlayer;

public class ButtonAdapter extends BaseAdapter{
	
	private MainActivity mainRef;
	private ArrayList<MyRingtone> ringtoneList;	
	private RingtonePlayer player;
	private int preClickPosition = -1; //上一次点击的按钮序号
	
	public ButtonAdapter(MainActivity ref, ArrayList<MyRingtone> list){
		mainRef = ref;
		this.ringtoneList = list;
		player = new RingtonePlayer(ref);
	}

	
	public int getCount() {
		// TODO Auto-generated method stub
		return this.ringtoneList.size();
	}

	
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.ringtoneList.get(arg0);
	}

	
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int currentPosition = position;
		ViewHolder holder;		
		if(convertView == null){
			
			LayoutInflater inflater = (LayoutInflater)mainRef.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item, parent, false);
			holder = new ViewHolder();
			holder.btn = (Button)convertView.findViewById(R.id.ringtone_btn);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.btn.setText(this.ringtoneList.get(position).getCustomizedName());
		holder.btn.setTextSize(12);
		holder.btn.setTag(Integer.valueOf(position));
		
		holder.btn.setOnClickListener(new OnClickListener(){

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("Current position: " + currentPosition);
				
				if(!player.isPlaying()){
					player.setFiletoPlay(ringtoneList.get(currentPosition).getRingtoneFilename());
					player.play();
					preClickPosition = currentPosition;
				}else{
					player.stop();
					
					if(preClickPosition != currentPosition){
						//点击同一个按钮，则停止播放当前的音频
						player.setFiletoPlay(ringtoneList.get(currentPosition).getRingtoneFilename());
						player.play();
						preClickPosition = currentPosition;
					}
					
				}
				
			}
			
		});
		
		mainRef.registerForContextMenu(holder.btn);
		return convertView;
	}
	
	public RingtonePlayer getRingtonePlayer(){
		return this.player;
	}

	static class ViewHolder{
		Button btn;
	}

}
