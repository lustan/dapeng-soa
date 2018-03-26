package com.isuwang.dapeng.tools.commands;

import com.isuwang.dapeng.tools.helps.MetaInfoHelper;
import org.clamshellcli.api.Command;
import org.clamshellcli.api.Context;
import org.clamshellcli.api.IOConsole;

import java.util.Map;

public class MetaInfoCmd implements Command {

    private static final String NAMESPACE = "syscmd";
    private static final String ACTION_NAME = "metaInfo";
    @Override
    public Descriptor getDescriptor() {
        return new Descriptor() {
            @Override
            public String getNamespace() {
                return NAMESPACE;
            }

            @Override
            public String getName() {
                return ACTION_NAME;
            }

            @Override
            public String getDescription() {
                return "通过服务名和版本号，获取元信息";
            }

            @Override
            public String getUsage() {
                return "metadata com.isuwang.soa.hello.service.HelloService 1.0.1";
            }

            @Override
            public Map<String, String> getArguments() {
                return null;
            }
        };
    }

    @Override
    public Object execute(Context ctx) {
        String[] args = (String[]) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);
        IOConsole c = ctx.getIoConsole();
        if(args != null) {
            try {
                MetaInfoHelper.getService(args);

            } catch (Exception ex) {
                c.writeOutput(String.format("%nUnable execute command: %s%n%n", ex.getMessage()));
                return null;
            }
        }
        return null;
    }

    @Override
    public void plug(Context plug) {

    }
}
