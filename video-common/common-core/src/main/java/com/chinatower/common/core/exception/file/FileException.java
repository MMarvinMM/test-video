package com.chinatower.common.core.exception.file;

import com.chinatower.common.core.exception.BaseException;

/**
 * 文件信息异常类
 *
 * @author chinatower
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
