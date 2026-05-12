package github.lukingyu.shortlink.base.entity.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import github.lukingyu.shortlink.base.entity.table.ShortLinkDO;
import lombok.Data;

import java.util.List;

@Data
public class ShortLinkRecycleBinPageReqDTO extends Page<ShortLinkDO> {

    /**
     * 分组标识
     */
    private List<String> gidList;
}