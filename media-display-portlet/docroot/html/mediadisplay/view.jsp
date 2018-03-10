<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>

<portlet:defineObjects />

<portlet:actionURL name="upload" var="uploadFileURL"></portlet:actionURL>

<aui:form action="<%= uploadFileURL %>" enctype="multipart/form-data" method="post">

	<aui:input type="file" name="fileupload" />
	
	<aui:button name="Save" value="Upload" type="submit" />

</aui:form>

<img src="${img_path }">