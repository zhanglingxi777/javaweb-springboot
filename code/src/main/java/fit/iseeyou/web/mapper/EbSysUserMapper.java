package fit.iseeyou.web.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fit.iseeyou.web.domain.EbSysUserDomain;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EbSysUserMapper extends BaseMapper<EbSysUserDomain> {

}
