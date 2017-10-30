package com.compuware.ispw.restapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import com.compuware.ispw.restapi.util.HttpRequestNameValuePair;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

/**
 * @author Martin d'Anjou
 */
public final class IspwRestApiRequestStep extends AbstractStepImpl {

    private @Nonnull String url;
	private boolean ignoreSslErrors = DescriptorImpl.ignoreSslErrors;
	private HttpMode httpMode                 = DescriptorImpl.httpMode;
    private String httpProxy                  = DescriptorImpl.httpProxy;
    private String validResponseCodes         = DescriptorImpl.validResponseCodes;
    private String validResponseContent       = DescriptorImpl.validResponseContent;
    private MimeType acceptType               = DescriptorImpl.acceptType;
    private MimeType contentType              = DescriptorImpl.contentType;
    private Integer timeout                   = DescriptorImpl.timeout;
    private Boolean consoleLogResponseBody    = DescriptorImpl.consoleLogResponseBody;
    private Boolean quiet                     = DescriptorImpl.quiet;
    private String authentication             = DescriptorImpl.authentication;
    private String requestBody                = DescriptorImpl.requestBody;
    private List<HttpRequestNameValuePair> customHeaders = DescriptorImpl.customHeaders;
	private String outputFile = DescriptorImpl.outputFile;
	private ResponseHandle responseHandle = DescriptorImpl.responseHandle;
	private String token = DescriptorImpl.token; //modified by pmisvz0

    @DataBoundConstructor
    public IspwRestApiRequestStep(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

	public boolean isIgnoreSslErrors() {
		return ignoreSslErrors;
	}

	@DataBoundSetter
	public void setIgnoreSslErrors(boolean ignoreSslErrors) {
		this.ignoreSslErrors = ignoreSslErrors;
	}

	@DataBoundSetter
    public void setHttpMode(HttpMode httpMode) {
        this.httpMode = httpMode;
    }

    public HttpMode getHttpMode() {
        return httpMode;
    }

	public String getToken() {
		return token;
	}

	//modified by pmisvz0
	@DataBoundSetter
	public void setToken(String token) {
		this.token = token;
	}
    
    @DataBoundSetter
    public void setHttpProxy(String httpProxy) {
        this.httpProxy = httpProxy;
    }
    //modify end
    
    public String getHttpProxy() {
        return httpProxy;
    }

    @DataBoundSetter
    public void setValidResponseCodes(String validResponseCodes) {
        this.validResponseCodes = validResponseCodes;
    }

    public String getValidResponseCodes() {
        return validResponseCodes;
    }

    @DataBoundSetter
    public void setValidResponseContent(String validResponseContent) {
        this.validResponseContent = validResponseContent;
    }

    public String getValidResponseContent() {
        return validResponseContent;
    }

    @DataBoundSetter
    public void setAcceptType(MimeType acceptType) {
        this.acceptType = acceptType;
    }

    public MimeType getAcceptType() {
        return acceptType;
    }

    @DataBoundSetter
    public void setContentType(MimeType contentType) {
        this.contentType = contentType;
    }

    public MimeType getContentType() {
        return contentType;
    }

    @DataBoundSetter
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getTimeout() {
        return timeout;
    }

    @DataBoundSetter
    public void setConsoleLogResponseBody(Boolean consoleLogResponseBody) {
        this.consoleLogResponseBody = consoleLogResponseBody;
    }

    public Boolean getConsoleLogResponseBody() {
        return consoleLogResponseBody;
    }

    @DataBoundSetter
    public void setQuiet(Boolean quiet) {
        this.quiet = quiet;
    }

    public Boolean getQuiet() {
        return quiet;
    }

    @DataBoundSetter
    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getAuthentication() {
        return authentication;
    }

    @DataBoundSetter
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestBody() {
        return requestBody;
    }

    @DataBoundSetter
    public void setCustomHeaders(List<HttpRequestNameValuePair> customHeaders) {
        this.customHeaders = customHeaders;
    }

    public List<HttpRequestNameValuePair> getCustomHeaders() {
        return customHeaders;
    }

	public String getOutputFile() {
		return outputFile;
	}

	@DataBoundSetter
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public ResponseHandle getResponseHandle() {
		return responseHandle;
	}


	@DataBoundSetter
	public void setResponseHandle(ResponseHandle responseHandle) {
		this.responseHandle = responseHandle;
	}

	@Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

	List<HttpRequestNameValuePair> resolveHeaders() {
		final List<HttpRequestNameValuePair> headers = new ArrayList<>();
		
//		if (contentType != null && contentType != MimeType.NOT_SET) {
//			headers.add(new HttpRequestNameValuePair("Content-type", contentType.getContentType().toString()));
//		}
		
		headers.add(new HttpRequestNameValuePair("Content-type", MimeType.APPLICATION_JSON.toString()));
		headers.add(new HttpRequestNameValuePair("Authorization", getToken()));
		
		if (acceptType != null && acceptType != MimeType.NOT_SET) {
			headers.add(new HttpRequestNameValuePair("Accept", acceptType.getValue()));
		}
		for (HttpRequestNameValuePair header : customHeaders) {
			String headerName = header.getName();
			String headerValue = header.getValue();
			boolean maskValue = headerName.equalsIgnoreCase("Authorization") ||
					header.getMaskValue();

			headers.add(new HttpRequestNameValuePair(headerName, headerValue, maskValue));
		}
		return headers;
	}

	@Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {
        public static final boolean ignoreSslErrors = IspwRestApiRequest.DescriptorImpl.ignoreSslErrors;
        public static final HttpMode httpMode                  = IspwRestApiRequest.DescriptorImpl.httpMode;
        public static final String   httpProxy                 = IspwRestApiRequest.DescriptorImpl.httpProxy;
        public static final String   validResponseCodes        = IspwRestApiRequest.DescriptorImpl.validResponseCodes;
        public static final String   validResponseContent      = IspwRestApiRequest.DescriptorImpl.validResponseContent;
        public static final MimeType acceptType                = IspwRestApiRequest.DescriptorImpl.acceptType;
        public static final MimeType contentType               = IspwRestApiRequest.DescriptorImpl.contentType;
        public static final int      timeout                   = IspwRestApiRequest.DescriptorImpl.timeout;
        public static final Boolean  consoleLogResponseBody    = IspwRestApiRequest.DescriptorImpl.consoleLogResponseBody;
        public static final Boolean  quiet                     = IspwRestApiRequest.DescriptorImpl.quiet;
        public static final String   authentication            = IspwRestApiRequest.DescriptorImpl.authentication;
        public static final String   requestBody               = IspwRestApiRequest.DescriptorImpl.requestBody;
        public static final String   token                     = IspwRestApiRequest.DescriptorImpl.token;
        public static final List <HttpRequestNameValuePair> customHeaders = Collections.<HttpRequestNameValuePair>emptyList();
        public static final String outputFile = "";
		public static final ResponseHandle responseHandle = ResponseHandle.STRING;

        public DescriptorImpl() {
            super(Execution.class);
        }

		@Override
		public String getFunctionName() {
			return "ispwRestApiRequest";
		}

		@Override
		public String getDisplayName() {
			return "Perform an ISPW Rest API Request and return a JSON object";
		}

        public ListBoxModel doFillHttpModeItems() {
            return HttpMode.getFillItems();
        }

        public ListBoxModel doFillAcceptTypeItems() {
            return MimeType.getContentTypeFillItems();
        }

        public ListBoxModel doFillContentTypeItems() {
            return MimeType.getContentTypeFillItems();
        }

		public ListBoxModel doFillResponseHandleItems() {
			ListBoxModel items = new ListBoxModel();
			for (ResponseHandle responseHandle : ResponseHandle.values()) {
				items.add(responseHandle.name());
			}
			return items;
		}

        public ListBoxModel doFillAuthenticationItems(@AncestorInPath Item project,
													  @QueryParameter String url) {
            return IspwRestApiRequest.DescriptorImpl.fillAuthenticationItems(project, url);
        }

        public FormValidation doCheckValidResponseCodes(@QueryParameter String value) {
            return IspwRestApiRequest.DescriptorImpl.checkValidResponseCodes(value);
        }

    }

    public static final class Execution extends AbstractSynchronousNonBlockingStepExecution<ResponseContentSupplier> {

        @Inject
        private transient IspwRestApiRequestStep step;

		@StepContextParameter
		private transient Run<?, ?> run;
		@StepContextParameter
		private transient TaskListener listener;

		@Override
		protected ResponseContentSupplier run() throws Exception {
			HttpRequestExecution exec = HttpRequestExecution.from(step,
					step.getQuiet() ? TaskListener.NULL : listener,
					this);

			Launcher launcher = getContext().get(Launcher.class);
			if (launcher != null) {
				return launcher.getChannel().call(exec);
			}

			return exec.call();
		}

        private static final long serialVersionUID = 1L;

		FilePath resolveOutputFile() {
			String outputFile = step.getOutputFile();
			if (outputFile == null || outputFile.trim().isEmpty()) {
				return null;
			}

			try {
				FilePath workspace = getContext().get(FilePath.class);
				if (workspace == null) {
					throw new IllegalStateException("Could not find workspace to save file outputFile: " + outputFile +
							". You should use it inside a 'node' block");
				}
				return workspace.child(outputFile);
			} catch (IOException | InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}

		public Item getProject() {
			return run.getParent();
		}
	}
}
