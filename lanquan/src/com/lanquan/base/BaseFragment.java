package com.lanquan.base;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**   
*    
* 项目名称：lanquan   
* 类名称：BaseFragment   
* 类描述：   自定义的继承了android.app.Fragment的抽象类，实现了findViewById和initView两个抽象方法，子类必须实现覆盖
* 创建人：张帅  
* 创建时间：2014-1-5 上午10:33:44   
* 修改人：张帅   
* 修改时间：2014-1-5 上午10:33:44   
* 修改备注：   
* @version    
*    
*/
public abstract class BaseFragment extends Fragment {
	public static final String TAG = BaseFragment.class.getSimpleName();
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 绑定控件id
	 */
	protected abstract void findViewById();

	/**
	 * 初始化控件
	 */
	protected abstract void initView();

}
