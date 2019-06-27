
public interface PluginDict {
	/**
	 * 服务注入提供 @Service
	 */
	public final static Class<?> PLUGIN_WIRED_SERVER = com.YaNan.frame.plugin.autowired.plugin.PluginWiredHandler.class;
	/**
	 *  资源类注入	@Resource
	 */
	public final static Class<?> RESOURCE_WIRED_SERVER = com.YaNan.frame.plugin.autowired.resource.ResourceWiredHandler.class;
	/**
	 *  属性注入 @Property
	 */
	public final static Class<?> PROPERTY_WIRED_SERVER = com.YaNan.frame.plugin.autowired.property.PropertyWiredHandler.class;
	/**
	 *  错误记录 @Error
	 */
	public final static Class<?> ERROR_WIRED_SERVER = com.YaNan.frame.plugin.autowired.exception.ErrorPlugsHandler.class; 
	/**
	 *  Quartz corn注解服务
	 */
	public final static Class<?> QUARTZ_MANAGER_SERVER = com.YaNan.frame.util.quartz.QuartzManager.class;
	/**
	 *  参数验证拦截器
	 */
	public final static Class<?> PARAMETER_VALITATION_SERVER = com.YaNan.frame.servlets.validator.ParameterValitationRegister.class; 
	/**
	 *  默认jsr 303 参数验证器
	 */
	public final static Class<?> DEFAULT_PARAMETER_VALIDATOR_SERVER = com.YaNan.frame.servlets.validator.DefaultParameterValidator.class; 
	/**
	 *  无状态Token拦截器
	 */
	public final static Class<?> TOKEN_HANDLER_SERVER = com.YaNan.frame.servlets.session.plugin.TokenHandler.class; 
	/**
	 * Token参数注入 @TokenAttribute
	 */
	public final static Class<?> TOKEN_PARAMETER_HANDLER = com.YaNan.frame.servlets.session.parameter.TokenParameterHandler.class; 
	/**
	 * 动态更新服务
	 */
	public final static Class<?> CLASS_HOT_UPDATER_SERVER = com.YaNan.frame.plugin.hot.ClassHotUpdater.class;
	/**
	 * 数据库服务
	 */
	public final static Class<?> PLUGIN_HIBERNATE_SERVER = com.YaNan.frame.hibernate.database.HibernateContextInit.class;
	
	/**
	 * mvc服务
	 */
	public final static Class<?> PLUGIN_MVC_SERVER = com.YaNan.frame.servlets.RestfulDispatcher.class;
	
}
