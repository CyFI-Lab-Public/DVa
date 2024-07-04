package com.company.symbolic.constraint;

import java.util.HashMap;

public class JavaMethodsHandler {
	static HashMap<String, IZ3Transfer> cms = new HashMap<String, IZ3Transfer>();

	static {
		// TODO Auto-generated method stub
		cms.put("<android.app.Activity: android.view.View findViewById(int)>", new IZ3Transfer_Activity_findViewById());
		cms.put("<android.view.View: android.view.View findViewById(int)>", new IZ3Transfer_Activity_findViewById());
		cms.put("<android.widget.EditText: android.text.Editable getText()>", new IZ3Transfer_JustTransfer());
		cms.put("<android.text.Editable: java.lang.String toString()>", new IZ3Transfer_JustTransfer());
		cms.put("<java.lang.String: java.lang.String trim()>", new IZ3Transfer_JustTransfer());
		cms.put("<java.lang.Object: java.lang.String toString()>", new IZ3Transfer_JustTransfer());
		cms.put("", new IZ3Transfer_JustTransfer());

		cms.put("<java.lang.String: int length()>", new IZ3Transfer_String_length());
		cms.put("<java.lang.String: boolean isEmpty()>", new IZ3Transfer_String_isEmpty());
		cms.put("<java.lang.String: boolean contains(java.lang.CharSequence)>", new IZ3Transfer_String_contains());
		cms.put("<java.lang.String: boolean contentEquals(java.lang.CharSequence)>", new IZ3Transfer_String_contentEquals());
		cms.put("<java.lang.String: boolean equals(java.lang.Object)>", new IZ3Transfer_String_equals());
		cms.put("<java.lang.String: boolean equalsIgnoreCase(java.lang.String)>", new IZ3Transfer_String_equalsIgnoreCase());
		cms.put("<java.lang.String: boolean matches(java.lang.String)>", new IZ3Transfer_String_matches());
		cms.put("<java.lang.String: boolean endsWith(java.lang.String)>", new IZ3Transfer_String_endsWith());
		cms.put("<java.lang.String: boolean startsWith(java.lang.String)>", new IZ3Transfer_String_startsWith());

		cms.put("<android.text.TextUtils: boolean isEmpty(java.lang.CharSequence)>", new IZ3Transfer_TextUtils_isEmpty());

		
		cms.put("<java.util.regex.Pattern: java.util.regex.Pattern compile(java.lang.String)>", new IZ3Transfer_Pattern_compile());
		cms.put("<java.util.regex.Pattern: java.util.regex.Matcher matcher(java.lang.CharSequence)>", new IZ3Transfer_Pattern_matcher());
		cms.put("<java.util.regex.Matcher: boolean matches()>", new IZ3Transfer_Matcher_matches());

	}

	public static boolean contains(String sign) {
		return cms.containsKey(sign);
	}

	public static IZ3Transfer getHandler(String sign) {
		return cms.get(sign);
	}
}
