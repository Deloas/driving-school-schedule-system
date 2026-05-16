/**
 * 通用 TypeScript 类型定义
 */

/** 后端统一响应格式 */
export interface ApiResult<T = unknown> {
  /** 业务状态码，200 表示成功 */
  code: number
  /** 提示信息 */
  message: string
  /** 响应数据 */
  data: T
}

/** 分页查询请求参数 */
export interface PageQuery {
  /** 当前页码，从 1 开始 */
  page: number
  /** 每页条数 */
  size: number
  /** 搜索关键词，可选 */
  keyword?: string
}

/** 分页查询响应 */
export interface PageResult<T> {
  /** 数据列表 */
  records: T[]
  /** 总条数 */
  total: number
  /** 当前页码 */
  page: number
  /** 每页条数 */
  size: number
}
