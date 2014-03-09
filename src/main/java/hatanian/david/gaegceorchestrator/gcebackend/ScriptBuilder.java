package hatanian.david.gaegceorchestrator.gcebackend;

import hatanian.david.gaegceorchestrator.domain.Execution;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ScriptBuilder {

    public static final String SCRIPT_SH = "script.sh";
    private static final Logger log = Logger.getLogger(ScriptBuilder.class.getName());

    public static String getScript(Execution execution, String returnUrl) {
        String scriptTemplate = loadScriptTemplate();
        scriptTemplate = scriptTemplate.replaceAll("%USER_SCRIPT_BUCKET%", execution.getUserScript().getBucket());
        scriptTemplate = scriptTemplate.replaceAll("%USER_SCRIPT_PATH%",execution.getUserScript().getPath());
        scriptTemplate = scriptTemplate.replaceAll("%TIMEOUT_HOURS%",Long.toString(convertMsToHours(execution.getUserScript().getTimeoutMs())));
        scriptTemplate = scriptTemplate.replaceAll("%RESULT_URL%",returnUrl);
        scriptTemplate = scriptTemplate.replaceAll("%EXECUTION_ID%",execution.getId());
        scriptTemplate = scriptTemplate.replaceAll("%RESULT_BUCKET%",execution.getResultBucket());
        scriptTemplate = scriptTemplate.replaceAll("%INSTANCE_NAME%",execution.getDiskAndInstanceName());
        scriptTemplate = scriptTemplate.replaceAll("%ZONE%",execution.getGceConfiguration().getZone());
        scriptTemplate = scriptTemplate.replaceAll("%PROJECT_ID%",execution.getProjectId());
        return scriptTemplate;
    }

    private static long convertMsToHours(long timeoutMs) {
        return timeoutMs / (60*60*1000);
    }

    private static String loadScriptTemplate() {
        InputStream scriptStream = null;
        BufferedReader scriptReader = null;
        StringWriter scriptWriter = null;
        try {
            scriptStream = ScriptBuilder.class.getClassLoader().getResourceAsStream(SCRIPT_SH);
            try {
                scriptReader = new BufferedReader(new InputStreamReader(scriptStream, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Unable to use UTF8 encoding to open script. This should not happen on the GAE environment", e);
            }
            scriptWriter = new StringWriter();
            IOUtils.copy(scriptReader,scriptWriter);
            return scriptWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read the script template from resource files. This should not happen.", e);
        } finally {
            if (scriptReader == null){
                if(scriptStream != null){
                    try {
                        scriptStream.close();
                    } catch (IOException e) {
                        log.log(Level.INFO, "Unable to close script template stream. This should not impact the application", e);
                    }
                }
            }else{
                try {
                    scriptReader.close();
                } catch (IOException e) {
                    log.log(Level.INFO, "Unable to close script template reader. This should not impact the application", e);
                }
            }

            if(scriptWriter != null){
                try {
                    scriptWriter.close();
                } catch (IOException e) {
                    log.log(Level.INFO, "Unable to close script template writer. This should not impact the application", e);
                }
            }
        }
    }
}
