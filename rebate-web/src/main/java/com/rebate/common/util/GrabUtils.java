package com.rebate.common.util;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.htmlcleaner.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ????????
 * 
 * @author liujiangping<br>
 *         2010-12-9 06:48:10
 * 
 */
public class GrabUtils {

	private static final Logger LOG = Logger.getLogger(GrabUtils.class);

	private static final String UTF_8 ="UTF-8";
	private static final String GBK ="GBK";
	public static TagNode analyzeURL(String url, HtmlCleaner cleaner)
			throws MalformedURLException, IOException {
		CleanerTransformations cleanerTransformations = new CleanerTransformations();
		TagTransformation tagTransformation = new TagTransformation("br");
		cleanerTransformations.addTransformation(tagTransformation);
		TagNode node = cleaner.clean(new URL(url));
		return node;
	}

	public static String getCharsetFromContentTypeString(String contentType) {
		if (contentType != null) {
			String pattern = "charset=([a-z\\d\\-]*)";
			Matcher matcher = Pattern
					.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(
							contentType);
			if (matcher.find()) {
				String charset = matcher.group(1);
				if (Charset.isSupported(charset)) {
					return charset;
				}
			}
		}

		return null;
	}

	public static String getCharsetFromContent(URL url) throws IOException {
		InputStream stream = url.openStream();
		byte chunk[] = new byte[2048];
		int bytesRead = stream.read(chunk);
		if (bytesRead > 0) {
			String startContent = new String(chunk);
			String pattern = "\\<meta\\s*http-equiv=[\\\"\\']content-type[\\\"\\']\\s*content\\s*=\\s*[\"']text/html\\s*;\\s*charset=([a-z\\d\\-]*)[\\\"\\'\\>]";
			Matcher matcher = Pattern
					.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(
							startContent);
			if (matcher.find()) {
				String charset = matcher.group(1);
				if (Charset.isSupported(charset)) {
					return charset;
				}
			}
		}

		return null;
	}

	public static String getContentEncoding(String _url) {
		HttpURLConnection conn = null;
		String charset = null;
		try {
			URL url = new URL(_url);
			charset = GBK;

			if (StringUtils.isBlank(charset)) {
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(2000);
				conn.connect();
				Map map = conn.getHeaderFields();
				for (Object key : map.keySet()) {
					if (key != null && key.equals("Content-Type")) {
						String val = map.get(key).toString().toLowerCase();
						int m = val.indexOf("charset=");
						if (m != -1) {
							charset = val.substring(m + 8).replace("]", "");
						}
					}
				}
			}

			if (StringUtils.isBlank(charset)) {
				StringBuffer sb = new StringBuffer();
				String line;
				try {
					BufferedReader in = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					while ((line = in.readLine()) != null) {
						sb.append(line);
					}
					in.close();
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				String context = sb.toString();
				String strbegin = "<meta";
				String strend = ">";
				String strtmp;
				int begin = context.indexOf(strbegin);
				int end = -1;
				int inttmp;
				while (begin > -1 && charset != null) {
					end = context.substring(begin).indexOf(strend);
					if (begin > -1 && end > -1) {
						strtmp = context.substring(begin, begin + end)
								.toLowerCase();
						inttmp = strtmp.indexOf("charset");
						if (inttmp > -1) {
							charset = strtmp.substring(inttmp + 7, end)
									.replace("=", "").replace("/", "").replace(
											"\"", "").replace("\'", "")
									.replace(" ", "");
						}
					}
					context = context.substring(begin);
					begin = context.indexOf(strbegin);
				}
			}

			if (charset == null) {
				charset = "GBK";
			}
			return charset;
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return "utf-8";

	}


	public static boolean isValidUrl(String strUrl) {
		try {
			URL url = new URL(strUrl);
			HttpURLConnection huConn = (HttpURLConnection) url.openConnection();
			huConn.setRequestMethod("HEAD");
			huConn.setConnectTimeout(2000);
			String strMessage = huConn.getResponseMessage();
			if (strMessage.equals("OK")) {
				huConn.disconnect();
				return true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " \n" + strUrl + "\n", e);
			return false;
		}
		return false;
	}

	/**
	 * xpath node
	 * 
	 * @return
	 */
	public static TagNode evaluateXPath(TagNode node, String xpath) {
		TagNode tNode = null;
		if (StringUtils.isNotBlank(xpath) && node != null) {
			try {
				Object obj[] = node.evaluateXPath(xpath);
				if (obj != null && obj.length != 0) {
					return (TagNode) obj[0];
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return tNode;
	}

	public static String getValueByEvaluateXPath(TagNode node, String xpath) {
		if (StringUtils.isNotBlank(xpath) && node != null) {
			try {
				Object obj[] = node.evaluateXPath(xpath);
				if (obj != null && obj.length != 0) {
					TagNode tNode = (TagNode) obj[0];
					if (tNode.getText() == null) {
						return null;
					}
					return tNode.getText().toString();
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return null;
	}

	public static TagNode[] evaluatesXPath(TagNode node, String xpath) {
		TagNode[] tNode = null;
		if (StringUtils.isNotBlank(xpath) && node != null) {
			try {
				Object obj[] = node.evaluateXPath(xpath);
				if (obj != null && obj.length != 0) {
					tNode = new TagNode[obj.length];
					for (int i = 0; i < obj.length; i++) {
						tNode[i] = (TagNode) obj[i];
					}
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return tNode;
	}

	public static String installUrl(String url, String cUrl) {
		try {
			String link = url;
			if (StringUtils.isBlank(cUrl)) {
				return "";
			}
			if (cUrl.startsWith("javascript")) {
				if (cUrl.indexOf("('") > 0 && cUrl.indexOf("')") > 0) {
					cUrl = cUrl.substring(cUrl.indexOf("('") + 2, cUrl
							.indexOf("')"));
				} else if (cUrl.indexOf("(\"") > 0 && cUrl.indexOf("\")") > 0) {
					cUrl = cUrl.substring(cUrl.indexOf("(\"") + 2, cUrl
							.indexOf("\")"));
				}

				if (cUrl.indexOf("(&apos;") > 0 && cUrl.indexOf("&apos;)") > 0) {
					cUrl = cUrl.substring(cUrl.indexOf("(&apos;") + 7, cUrl
							.indexOf("&apos;)"));
				} else if (cUrl.indexOf("(&quot;") > 0
						&& cUrl.indexOf("&quot;)") > 0) {
					cUrl = cUrl.substring(cUrl.indexOf("(&quot;") + 7, cUrl
							.indexOf("&quot;)"));
				}

			}
			if (cUrl.startsWith("http://")) {
				return cUrl;
			} else if (cUrl.startsWith("/")) {
				int xIndex = url.indexOf("/", "http://".length() + 5);
				link = link.substring(0, xIndex);
				return link + cUrl;
			} else if (!cUrl.startsWith("./") && !cUrl.startsWith("../")) {
				link = url.substring(0, url.lastIndexOf("/"));
			}
			while (cUrl.startsWith("./") || cUrl.startsWith("../")) {
				if (cUrl.startsWith("./")) {
					link = link.substring(0, link.lastIndexOf("/"));
					cUrl = cUrl.replaceFirst("./", "");
				} else {
					link = link.substring(0, link.lastIndexOf("/"));
					cUrl = cUrl.replaceFirst("../", "");
				}
			}
			link = link + "/" + cUrl;
			return link;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return url;

	}

	public static String getUrlByNum(String url, int num) {
		String suffix = url.substring(url.lastIndexOf("."));
		String prefix = url.substring(0, url.lastIndexOf("."));
		return prefix + "_" + num + suffix;
	}

	public static void nodeFilter(TagNode node) {
		TagNode aNodes[] = node.getElementsByName("a", true);
		TagNode scriptNodes[] = node.getElementsByName("script", true);
		TagNode imgNodes[] = node.getElementsByName("img", true);
		TagNode frameNodes[] = node.getElementsByName("frame", true);
		TagNode styleNodes[] = node.getElementsByName("style", true);
		if (aNodes != null && aNodes.length != 0) {
			for (int i = 0; i < aNodes.length; i++) {
				aNodes[i].getParent().removeChild(aNodes[i]);
			}
		}
		if (scriptNodes != null && scriptNodes.length != 0) {
			for (int i = 0; i < scriptNodes.length; i++) {
				scriptNodes[i].getParent().removeChild(scriptNodes[i]);
			}
		}
		if (imgNodes != null && imgNodes.length != 0) {
			for (int i = 0; i < imgNodes.length; i++) {
				imgNodes[i].getParent().removeChild(imgNodes[i]);
			}
		}
		if (frameNodes != null && frameNodes.length != 0) {
			for (int i = 0; i < frameNodes.length; i++) {
				frameNodes[i].getParent().removeChild(frameNodes[i]);
			}
		}
		if (styleNodes != null && styleNodes.length != 0) {
			for (int i = 0; i < styleNodes.length; i++) {
				styleNodes[i].getParent().removeChild(styleNodes[i]);
			}
		}
	}

	public static String innerHtmlByNode(TagNode node, HtmlCleaner cleaner,
			boolean isFiltering) {
		Map attMap = new HashMap(node.getAttributes());
		for (Object key : attMap.keySet()) {
			node.removeAttribute(key.toString());
		}
		if (isFiltering)
			nodeFilter(node);
		CleanerProperties props = cleaner.getProperties();
		props.setOmitXmlDeclaration(true);
		props.setUseCdataForScriptAndStyle(false);
		try {
			String str = new CompactXmlSerializer(props).getXmlAsString(node);
			if (str != null) {
				str = str.replaceFirst("<" + node.getName() + ">", "");
			}
			if (str != null && str.endsWith("</" + node.getName() + ">")) {
				str = str.substring(0, str.length()
						- ("</" + node.getName() + ">").length());
			}
			str = replaceErrorMark(str);
			return str;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 *
	 * @param node
	 * @param cleaner
	 * @param xpath
	 * @return
	 */
	public static String innerHtmlByNode(TagNode node, HtmlCleaner cleaner,
			String xpath) {
		try {
			Object obj[] = node.evaluateXPath(xpath);
			StringBuffer sb = new StringBuffer();
			if (obj != null && obj.length > 0) {
				for (Object object : obj) {

					String str = innerHtmlByNode((TagNode) object, cleaner);
					if (str != null) {
						sb.append(str);
					}
				}
				node = (TagNode) obj[0];
			}
			return sb.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * ??????html?
	 * 
	 * @param node
	 * @return
	 */
	public static String innerHtmlByNode(TagNode node, HtmlCleaner cleaner) {
		Map attMap = new HashMap(node.getAttributes());
		for (Object key : attMap.keySet()) {
			node.removeAttribute(key.toString());
		}
		nodeFilter(node);
		CleanerProperties props = cleaner.getProperties();
		props.setOmitXmlDeclaration(true);
		props.setUseCdataForScriptAndStyle(false);
		String str = null;
		try {
			str = new CompactXmlSerializer(props).getXmlAsString(node);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		if (str != null) {
			if (!"p".equals(node.getName())) {
				str = str.replaceFirst("<" + node.getName() + ">", "");
			}

		}
		if (str != null && str.endsWith("</" + node.getName() + ">")) {
			if (!"p".equals(node.getName())) {
				str = str.substring(0, str.length()
						- ("</" + node.getName() + ">").length());
			}
		}
		if (str != null) {
			str = replaceErrorMark(str);
		}
		return str;
	}


	public static String getValueByEvaluateXPath2(Element root, String xpath) {
		if (StringUtils.isNotBlank(xpath) && root != null) {
			try {
				List<Element> nodes = root.selectNodes(xpath);
				StringBuffer sb = new StringBuffer();
				if (null == nodes || nodes.size() <= 0) {
					return null;
				}
				for (Element element : nodes) {
					if (null != sb) {
						sb.append(innerHtmlByElement(element));
						int lastp = sb.length()-4;
						if (nodes.size() > 1 && sb.lastIndexOf("</p>")!=lastp) {
							sb.append("&nbsp;");
						}
					}

				}

				return sb.toString();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return null;
	}

	public static TagNode analyzeURL2(String urlStr, HtmlCleaner cleaner)
			throws MalformedURLException, IOException {
		URL url = new URL(urlStr);
		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

		String charset = null;
		charset = UTF_8;
		TagNode node = cleaner.clean(url, charset);
		return node;
	}

	/**
	 * ??????HTML?
	 * 
	 * @param node
	 * @return
	 */
	public static String innerHtmlByElement(Element node) {
		String str = node.asXML();
		if (str != null) {
			if (!"p".equals(node.getName())) {
				str = str.substring(str.indexOf(">") + 1, str.length());
			}

		}
		if (str != null && str.endsWith("</" + node.getName() + ">")) {
			if (!"p".equals(node.getName())) {
				str = str.substring(0, str.length()
						- ("</" + node.getName() + ">").length());
			}
		}
		if (str != null) {
			str = replaceErrorMark(str);
		}
		return str;
	}

	public static List<Element> evaluatesXPath2(Element node, String xpath) {
		List<Element> elements = null;
		if (StringUtils.isNotBlank(xpath) && node != null) {
			try {
				elements = node.selectNodes(xpath);

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return elements;
	}

	/**
	 * ?滻???????
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceErrorMark(String str) {
		str = str.replaceAll("&amp;gt", "&gt");
		str = str.replaceAll("&amp;lt", "&lt");
		str = str.replaceAll("&amp;ge", "&ge");
		str = str.replaceAll("&amp;le", "&le");
		str = str.replaceAll("&amp;bull", "&bull");
		str = str.replaceAll("&amp;apos", "&apos");
		str = str.replaceAll("&amp;quot", "&quot");
		str = str.replaceAll("\\?\\?\\?\\?", "&nbsp;&nbsp;&nbsp;&nbsp;");
		str = str.replaceAll("\n", "<br/>");
		str = toBlank(str);
		return str;
	}

	/**
	 * ???????????????????&nbsp;
	 * 
	 * @param str
	 * @return
	 */
	public static String toBlank(String str) {
		StringBuffer sb = new StringBuffer();
		for (char c : str.toCharArray()) {
			int ascll = (int) c;
			if (160 == c) {
				sb.append("&nbsp;");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		// System.out
		// .println(isValidUrl("http://vip.book.sina.com.cn/book/chapter_102852_70084.html"));

		//
		// System.out.println(installUrl(
		// "http://cathay.ce.cn/lzk/mingshi/index.shtml", "1.shtml"));

		HtmlCleaner cleaner = new HtmlCleaner();
		// CleanerTransformations cleanerTransformations = new
		// CleanerTransformations();
		// TagTransformation tagTransformation = new

		// cleanerTransformations.addTransformation(tagTransformation);
		// cleaner.setTransformations(cleanerTransformations);

		// CleanerProperties props = cleaner.getProperties();
		// TagNode node = cleaner.clean(new URL(
		// "http://lz.book.sohu.com/chapter-17342-113739367.html"),
		// "gb2312");

		//		
		// props.setOmitXmlDeclaration(true);
		//
		// String str = new CompactXmlSerializer(props).getXmlAsString(node);
		// System.out.println(innerHtmlByNode(node, cleaner,
		// "/body/div[5]/div/div[3]/p"));

		// try {
		// TagNode node = cleaner
		// .clean("<p><b><script language='javascript' type='text/javascript'

		// node = node.findElementByName("body", true);
		// // evalContentToken(node);
		// for (Object tNode : node.getChildren()) {
		// System.out.println(tNode);
		// }
		//
		// // node = node.findElementByName("p", true);
		//
		// // System.out.println(innerHtmlByNode(node, cleaner, true));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// try {
		// TagNode node = new HtmlCleaner()
		// .clean("http://lz.book.sohu.com/lz_info.php?bookid=17342");
		// // System.out.println(node);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// while (true) {
		// long t = System.currentTimeMillis();
		// System.out.println(getFileEncoding(new URL(
		// "http://lz.book.sohu.com/lz_info.php?bookid=17342")));
		// System.out.println(System.currentTimeMillis() - t);
		// }

		// String str="????aaaa????aaaa?";

		System.out
				.println(installUrl(
						"http://book.dzwww.com/book/1/nizhuzaiaiqingdenayilou_-654.html",
						"javascript:g('a.html')"));
	}
}
