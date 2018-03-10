package com.encephalsparks.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.DuplicateFileException;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.liferay.portlet.documentlibrary.util.DLUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Portlet implementation class MediaDisplay
 */
public class MediaDisplay extends MVCPortlet {
 
	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		// TODO Auto-generated method stub

		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		try {
			//FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(11254);
			
			Folder folder = DLAppLocalServiceUtil.getFolder(themeDisplay.getScopeGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "app_content");
			List<FileEntry> fileEntryList = DLAppServiceUtil.getFileEntries(themeDisplay.getScopeGroupId(), folder.getFolderId());
			if(fileEntryList != null && fileEntryList.size()>0){
				FileEntry fileEntry = fileEntryList.get(fileEntryList.size()-1);
				String img_path = DLUtil.getPreviewURL(fileEntry, fileEntry.getFileVersion(), themeDisplay,
						StringPool.BLANK, false, true);
				request.setAttribute("img_path", img_path);
			}
			else{
				request.setAttribute("img_path", "");
			}
			
		} catch(ArrayIndexOutOfBoundsException | NoSuchFileEntryException | NoSuchFolderException exp){
			request.setAttribute("img_path", "");
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.render(request, response);
	}

	public void upload(ActionRequest request, ActionResponse response) throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		String fileInputName = "fileupload";

		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);

		if (uploadRequest.getSize(fileInputName) == 0) {
			throw new Exception("Received file is 0 bytes!");
		}

		// Get the uploaded file as a file.
		File uploadedFile = uploadRequest.getFile(fileInputName);
		String fileName = uploadRequest.getFileName(fileInputName);
		// long groupId = themeDisplay.getScopeGroupId();
		long repositoryId = themeDisplay.getScopeGroupId();
		long parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID; // or
																			// 0
		String mimeType = MimeTypesUtil.getContentType(uploadedFile);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(DLFolder.class.getName(), request);
		Folder folder = null;
		try{
			
			folder = DLAppLocalServiceUtil.getFolder(repositoryId, parentFolderId, "app_content");
		}catch(NoSuchFolderException exp){
			
			folder = DLAppLocalServiceUtil.addFolder(themeDisplay.getDefaultUserId(),repositoryId,
					parentFolderId, "app_content", "Storing my content",serviceContext);
		}
		try{
			DLAppLocalServiceUtil.addFileEntry(themeDisplay.getDefaultUserId(), repositoryId, folder.getFolderId(), fileName, mimeType, fileName, fileName, fileName, uploadedFile, serviceContext);
		}catch(DuplicateFileException exp){
			
			FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(repositoryId, folder.getFolderId(), fileName);
			DLAppLocalServiceUtil.deleteFileEntry(fileEntry.getFileEntryId());
			DLAppLocalServiceUtil.addFileEntry(themeDisplay.getDefaultUserId(), repositoryId, folder.getFolderId(), fileName, mimeType, fileName, fileName, fileName, uploadedFile, serviceContext);
		}		
		//FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(11254);
		
		//DLAppLocalServiceUtil.updateFileEntry(themeDisplay.getDefaultUserId(), 11254, fileName, mimeType, fileName, "", "",
		//		false, uploadedFile, serviceContext);
		
	}

}
