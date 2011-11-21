
var URLFetchService = Packages.com.google.appengine.api.urlfetch.URLFetchService;
var URLFetchServiceFactory = Packages.com.google.appengine.api.urlfetch.URLFetchServiceFactory;
var HTTPMethod = Packages.com.google.appengine.api.urlfetch.HTTPMethod;
var HTTPRequest = Packages.com.google.appengine.api.urlfetch.HTTPRequest;

var URL = "https://maps.googleapis.com/maps/api/place/details/json";
var KEY = "AIzaSyChvQ_xuhslumwd1RV9OjVYkt3DH5e-iEg";

function doGet(req, res) {
	var url = URL+"?"+req.getQueryString()+"&key="+KEY;

	var fetcher = URLFetchServiceFactory.getURLFetchService();
	var request = new HTTPRequest(new java.net.URL(url), HTTPMethod.GET);
	var response = fetcher.fetch(request);
	var status = response.getResponseCode();
	var resbody = response.getContent();

	res.setStatus(status);
	res.setContentType("application/json");
	res.outputStream.write(resbody);
	res.outputStream.flush();
}
