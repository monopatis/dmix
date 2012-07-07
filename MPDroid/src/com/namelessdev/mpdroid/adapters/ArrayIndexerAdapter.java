package com.namelessdev.mpdroid.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.a0z.mpd.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

//Stolen from http://www.anddev.org/tutalphabetic_fastscroll_listview_-_similar_to_contacts-t10123.html
//Thanks qlimax !

public class ArrayIndexerAdapter extends ArrayAdapter<Item> implements SectionIndexer {

	HashMap<String, Integer> alphaIndexer;
	String[] sections;
	final int textViewResourceId;
	LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public ArrayIndexerAdapter(Context context, int textViewResourceId, List<? extends Item> items) {
		super(context, textViewResourceId, (List<Item>) items);
		if (!(items instanceof ArrayList<?>))
			throw new RuntimeException("Items must be contained in an ArrayList<Item>");

		this.textViewResourceId=textViewResourceId;

		// here is the tricky stuff
		alphaIndexer = new HashMap<String, Integer>(); 
		// in this hashmap we will store here the positions for
		// the sections

		int size = items.size();
		for (int i = size - 1; i >= 0; i--) {
			Item element = items.get(i);
			alphaIndexer.put(element.sort().substring(0, 1).toUpperCase(), i);
		//We store the first letter of the word, and its index.
		//The Hashmap will replace the value for identical keys are putted in
		} 

		// now we have an hashmap containing for each first-letter
		// sections(key), the index(value) in where this sections begins

		// we have now to build the sections(letters to be displayed)
		// array .it must contains the keys, and must (I do so...) be
		// ordered alphabetically

		Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
		// cannot be sorted...

		Iterator<String> it = keys.iterator();
		ArrayList<String> keyList = new ArrayList<String>(); // list can be
		// sorted

		while (it.hasNext()) {
			String key = it.next();
			keyList.add(key);
		}

		Collections.sort(keyList);

		sections = new String[keyList.size()]; // simple conversion to an
		// array of object
		keyList.toArray(sections);

		// ooOO00K !

	}

	@Override
	public int getPositionForSection(int section) {
		// Log.v("getPositionForSection", ""+section);
		String letter = sections[section >= sections.length ? sections.length - 1 : section];

		return alphaIndexer.get(letter);
	}

	@Override
	public int getSectionForPosition(int position) {
		if(sections.length == 0)
			return -1;
		
		if(sections.length == 1)
			return 1;
		
		for(int i = 0; i < (sections.length - 1); i ++) {
			int begin = alphaIndexer.get(sections[i]);
			int end = alphaIndexer.get(sections[i]) - 1;
			if(position >= begin && position < end)
				return i;
		}
		return sections.length - 1;
	}

	@Override
	public Object[] getSections() {

		return sections; // to string will be called each object, to display
		// the letter
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row;

		if (null == convertView) {
            if (null==inflater) {
                inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
			row = inflater.inflate(textViewResourceId, null);
		} else {
			row = convertView;
		}

		TextView tv = (TextView) row.findViewById(android.R.id.text1);
		tv.setText(getItem(position).mainText());
		tv = (TextView) row.findViewById(android.R.id.text2);
		if (null!=tv) {
			tv.setText(getItem(position).subText());
		}
		return row;
	}
}
