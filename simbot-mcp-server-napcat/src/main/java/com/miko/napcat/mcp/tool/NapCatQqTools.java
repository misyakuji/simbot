package com.miko.napcat.mcp.tool;


import com.miko.napcat.service.NapCatApiService;
import com.miko.napcat.service.message.ext.SendGroupMsgService;
import com.miko.service.SendGroupMsgRequest;
import com.miko.service.SendGroupMsgResponse;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * NapCat QQ 能力的 MCP 服务封装（供AI调用）
 */

@Component
@RequiredArgsConstructor
public class NapCatQqTools {

    private final NapCatApiService napCatApiService;
    private final SendGroupMsgService sendGroupMsgService;

    @McpTool(name = "sendGroupAt", description = "在QQ群中@某个成员")
    public void sendGroupAt(String groupId, String atQq) {
        SendGroupMsgRequest request = new SendGroupMsgRequest();
        request.setGroupId(groupId);
        request.setMessage(
                new SendGroupMsgRequest.Message(
                        "at",
                        new SendGroupMsgRequest.AtData(atQq, "string")
                )
        );

        SendGroupMsgResponse resp = sendGroupMsgService.sendGroupAt(request);
        System.out.println(resp.getStatus());
    }


}