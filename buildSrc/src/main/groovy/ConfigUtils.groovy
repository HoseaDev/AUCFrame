import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

import java.text.SimpleDateFormat

class ConfigUtils {
    private List<TaskInfo> taskInfoList = []
    private long startBuildMillis

    static addBuildListener(Gradle g) {
        g.addBuildListener(new BuildListener() {
            @Override
            void buildStarted(Gradle gradle) {
                GLog.d("buildStarted")
            }

            @Override
            void settingsEvaluated(Settings settings) {
                GLog.d("settingsEvaluated")
                startBuildMillis = System.currentTimeMillis()
                includeModule(settings)
            }

            @Override
            void projectsLoaded(Gradle gradle) {
                GLog.d("projectsLoaded")
                generateDep(gradle)
                gradle.addProjectEvaluationListener(new ProjectEvaluationListener() {
                    @Override
                    void beforeEvaluate(Project project) {
                        GLog.d("beforeEvaluate")
                        if (project.subprojects.isEmpty()) {
                            if (project.name=="app"){
                                GLog.l(project.toString() + " applies buildApp.gradle")
                                project.apply {
                                    from "${project.rootDir.path}/buildApp.gradle"
                                }


                            }else {
                                GLog.l(project.toString() + " applies buildLib.gradle")
                                project.apply {
                                    from "${project.rootDir.path}/buildLib.gradle"
                                }
                            }
                        }



                    }

                    @Override
                    void afterEvaluate(Project project, ProjectState projectState) {
                        GLog.d("afterEvaluate")
                    }
                })
            }

            @Override
            void projectsEvaluated(Gradle gradle) {
                GLog.d("projectsEvaluated")
                gradle.addListener(new TaskExecutionListener() {
                    @Override
                    void beforeExecute(Task task) {
                        task.ext.startTime = System.currentTimeMillis()
                    }
                    @Override
                    void afterExecute(Task task, TaskState state) {
                        def exeDuration = System.currentTimeMillis() - task.ext.startTime
                        if (exeDuration >= 100) {
                            taskInfoList.add(new TaskInfo(task, exeDuration))
                        }
                    }
                })

            }

            @Override
            void buildFinished(BuildResult buildResult) {
                GLog.d("buildFinished")
                if (!taskInfoList.isEmpty()) {
                    Collections.sort(taskInfoList, new Comparator<TaskInfo>() {
                        @Override
                        int compare(TaskInfo t, TaskInfo t1) {
                            return t1.exeDuration - t.exeDuration
                        }
                    })
                    StringBuilder sb = new StringBuilder()
                    int buildSec = (System.currentTimeMillis() - startBuildMillis) / 1000;
                    int m = buildSec / 60;
                    int s = buildSec % 60;
                    def timeInfo = (m == 0 ? "${s}s" : "${m}m ${s}s (${buildSec}s)")
                    sb.append("BUILD FINISHED in $timeInfo")
                    taskInfoList.each {
                        sb.append(String.format("%7sms %s\n", it.exeDuration, it.task.path))
                    }
                    def content = sb.toString()
                    GLog.l(content)
                    File file = new File(result.gradle.rootProject.buildDir.getAbsolutePath(),
                            "build_time_records_" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".txt")
                    FileUtils.write(file, content)
                }
            }
        })
    }
/**
 * 根据DepConfig生成Dep
 */
    private static generateDep(Gradle gradle) {
        def config = getDepConfigByFilter(new DepConfigFilter() {
            @Override
            boolean accept(String name, DepConfig config) {
                if (config.useLocal) {//如果使用的是本地模块，那么把它转化为project
                    config.dep = gradle.rootProject.findProject(config.localPath)
                } else {// 如果是远端依赖，那就直接使用无端依赖即可
                    config.dep = config.remotePath
                }
                return true
            }
        })
        GLog.l("generateDep = ${GLog.object2String(config)}")
    }
//    private static includeModule(Settings settings) {
////        settings.include ':lib:base', ':lib:common',
////                ':feature:feature0:export', ':feature:feature1:export',
////                ':feature:feature0:pkg', ':feature:feature1:pkg',
////                ':feature:feature0:app', ':feature:feature1:app',
////                ':feature:launcher:app'
//
//
//
//
//
//
//    }

    /**
     * 在settings.gradle中根据appConfig和pkgConfig来include本地模块
     * */
    private static includeModule(Settings settings) {
        def config = getDepConfigByFilter(new DepConfigFilter() {
            @Override
            boolean accept(String name, DepConfig config) {
                GLog.l("includeModule = "+name)

                if (Config.pkgConfig.isEmpty()) {
                    Config.depConfig.feature.mock.isApply = false
                }
                if (name.endsWith('.app')) {// 如果最终是app的话
                    def appName = name.substring('feature.'.length(), name.length()
                            - 4)// ឴获取app模块名字
                    if (!Config.appConfig.contains(appName)) {//如果Config.appConfig不存在。那就不让他依赖
                        config.isApply = false
                    }
                }
                if (!Config.pkgConfig.isEmpty()) {// 如果Config.pkgConfig不为空，说明kpg调试模式
                    if (name.endsWith('.pkg')) {//如果是pkg的话
                        def pkgName = name.substring('feature.'.length(),
                                name.length() - 4)//获取pkg模块名字
                        if (!Config.pkgConfig.contains(pkgName)) {//如果Config.pkgConfig中不存在，那就不让它依赖
                            config.isApply = false
                        }
                    }
                }
                // 过滤出本地并且apply模块
                if (!config.isApply) return false
                if (!config.useLocal) return false
                if (config.localPath == "") return false
                return true
            }
        }).each { _, cfg ->//把本地include进去
            GLog.l("cfg.localPath ="+cfg.localPath)
            settings.include cfg.localPath
        }
        GLog.l("includeModule = ${GLog.object2String(config)}")
    }

    /**
     * 根据过滤器来取DepConfig
     */
    static Map<String, DepConfig> getDepConfigByFilter(DepConfigFilter filter) {
        return _getDepConfigByFilter("", Config.depConfig, filter)
    }
    private static _getDepConfigByFilter(String namePrefix, Map map, DepConfigFilter
            filter) {
        def depConfigList = [:]//结果 Map
        for (Map.Entry entry : map.entrySet()) {
            def (name, value) = [entry.getKey(), entry.getValue()]
            if (value instanceof Map) {//如果值是Map类型就加到结果Map中
                namePrefix += (name + '.')
                depConfigList.putAll(_getDepConfigByFilter(namePrefix, value,
                        filter))
                namePrefix -= (name + '.')
                continue
            }
            def config = value as DepConfig
            if (filter == null || filter.accept(namePrefix + name, config)) {
                depConfigList.put(namePrefix + name, config)//符合过滤条件就加到结束Map中
            }
        }
        return depConfigList
    }

    interface DepConfigFilter {
        boolean accept(String name, DepConfig config);
    }

    static getApplyPkgs() {
        def applyPkgs = getDepConfigByFilter(new DepConfigFilter() {
            @Override
            boolean accept(String name, DepConfig config) {
                if (!config.isApply) return false
                return name.endsWith(".pkg")
            }
        })
        GLog.d("getApplyPkgs = ${GLog.object2String(applyPkgs)}")
        return applyPkgs
    }

    static getApplyExports() {
        def applyExports = getDepConfigByFilter(new DepConfigFilter() {
            @Override
            boolean accept(String name, DepConfig config) {
                if (!config.isApply) return false
                return name.endsWith(".export")
            }
        })
        GLog.d("getApplyExports = ${GLog.object2String(applyExports)}")
        return applyExports
    }

    // ConfigUtils
    static getApplyPlugins() {
        def plugins = getDepConfigByFilter(new DepConfigFilter() {
            @Override
            boolean accept(String name, DepConfig config) {
                if (!name.startsWith("plugin.")) return false
                if (!config.isApply) return false
                return true
            }
        })
        GLog.d("getApplyPlugins = ${GLog.object2String(plugins)}")
        return plugins
    }
}