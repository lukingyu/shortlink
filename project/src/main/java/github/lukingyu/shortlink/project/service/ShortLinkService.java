package github.lukingyu.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkPageReqDTO;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkUpdateReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkGroupCountQueryRespDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkPageRespDTO;
import github.lukingyu.shortlink.base.entity.table.ShortLinkDO;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkCreateReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkCreateRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建信息
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询短链接请求参数
     * @return 短链接分页返回结果
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 查询短链接分组内数量
     *
     * @param requestParam 查询短链接分组内数量请求参数
     * @return 查询短链接分组内数量响应
     */
    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam);

    /**
     * 更新短链接
     *
     * @param requestParam 修改短链接请求参数
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 短链接跳转
     *
     * @param shortUri 短链接后缀
     * @param request  HTTP 请求
     * @param response HTTP 响应
     */
    void redirect(String shortUri, ServletRequest request, ServletResponse response) throws IOException;
}