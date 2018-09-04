package com.eway.concatpeople;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.eway.concatpeople.sortlist.SortModel;

/**
 * @Description:用来处理集合中数据的显示与排序
 * @author http://blog.csdn.net/finddreams
 */ 
public class SortAdapter extends BaseAdapter implements SectionIndexer{
	public List<SortModel> list = null;
	private Context mContext;
	
	public SortAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		this.list = list;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.phone_constacts_item, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.icon = (ImageTextView) view.findViewById(R.id.icon);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		// 每次渲染一个item，先获取当前list中position位置的，首字母ASCII
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现 ，
		// posttion是当前要渲染的位置，但是通过getPositionforSection()返回的是字母第一次出现的位置，只有在第一次出现的时候、
		// 匹配，其余的获取的位置肯定和postion不同，所以第一次的设置显示，其余的隐藏
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
	
		viewHolder.tvTitle.setText(this.list.get(position).getName());  // 设置具体内容
		viewHolder.icon.setText(this.list.get(position).getName());     //  设置图片中的文字
		viewHolder.icon.setIconText(mContext,this.list.get(position).getName());  //
		return view;

	}
	


	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageTextView icon;
		
	}


	/**
	 * 根据ListView的当前位置，获取分类的首字母的Char ascii值
	 * 功能:通过listview中的位置，获取其所在分组
	 * @return  字母的ASCII
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);  // 获取list中的positon的数据对象的首字母，得到它的ASCII值
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 * 功能：通过分组位置号，获取该分组首字母所在的listview的位置
	 * 自动将'字母'转化为int的ASCII
	 * @ param section 传递过来的字母
	 * @return 是在list 中的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {  // 通过for，找到list 数据中第一个字母
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0); // toUpperCase()是将转化为大写字母
			if (firstChar == section) {  // 如果list中第一个字母等于给定的字母，则返回这个list的位置
				return i;
			}
		}
		
		return -1;   // 找不到，返回-1；
	}
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}


}