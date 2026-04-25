"""
性能测试报告生成器模块

该模块提供了性能测试报告的生成、分析和导出功能，包括：
- 统计数据计算（平均值、中位数、P95、P99）
- 性能基准检查
- 性能图表生成
- HTML和JSON报告导出
- 性能瓶颈分析和优化建议
"""

import json
import statistics
from datetime import datetime
from typing import Dict, List, Optional, Any, Tuple
from dataclasses import dataclass, field, asdict
import os


@dataclass
class PerformanceMetrics:
    """性能指标数据类"""
    total_requests: int = 0
    total_failures: int = 0
    total_success: int = 0
    avg_response_time: float = 0.0
    min_response_time: float = 0.0
    max_response_time: float = 0.0
    median_response_time: float = 0.0
    p95_response_time: float = 0.0
    p99_response_time: float = 0.0
    requests_per_second: float = 0.0
    failure_rate: float = 0.0
    concurrent_users: int = 0
    test_duration: float = 0.0


@dataclass
class PerformanceReport:
    """性能测试报告数据类"""
    test_name: str
    test_start_time: str
    test_end_time: str
    test_duration: float
    config: Dict[str, Any]
    metrics: PerformanceMetrics
    benchmark_results: Dict[str, Any]
    bottlenecks: List[str] = field(default_factory=list)
    recommendations: List[str] = field(default_factory=list)
    detailed_stats: Dict[str, Any] = field(default_factory=dict)


class PerformanceReporter:
    """
    性能测试报告生成器
    
    提供性能测试数据的统计、分析和报告生成功能
    """
    
    def __init__(self, benchmark_config: Optional[Any] = None):
        """
        初始化报告生成器
        
        Args:
            benchmark_config: 性能基准配置
        """
        self.benchmark_config = benchmark_config
        self.test_results: List[Dict[str, Any]] = []
    
    def generate_report(self, test_results: Dict[str, Any]) -> PerformanceReport:
        """
        生成性能测试报告
        
        Args:
            test_results: 测试结果数据
        
        Returns:
            性能测试报告对象
        """
        metrics = self._extract_metrics(test_results)
        benchmark_results = self.check_performance_benchmark(test_results)
        bottlenecks = self._analyze_bottlenecks(metrics, benchmark_results)
        recommendations = self._generate_recommendations(metrics, bottlenecks)
        
        report = PerformanceReport(
            test_name=test_results.get("test_name", "Performance Test"),
            test_start_time=test_results.get("start_time", ""),
            test_end_time=test_results.get("end_time", ""),
            test_duration=metrics.test_duration,
            config=test_results.get("config", {}),
            metrics=metrics,
            benchmark_results=benchmark_results,
            bottlenecks=bottlenecks,
            recommendations=recommendations,
            detailed_stats=test_results.get("stats", {}),
        )
        
        return report
    
    def _extract_metrics(self, test_results: Dict[str, Any]) -> PerformanceMetrics:
        """
        从测试结果中提取性能指标
        
        Args:
            test_results: 测试结果数据
        
        Returns:
            性能指标对象
        """
        stats = test_results.get("stats", {})
        
        total_requests = stats.get("num_requests", 0)
        total_failures = stats.get("num_failures", 0)
        total_success = total_requests - total_failures
        
        response_times = stats.get("response_times", {})
        avg_response_time = stats.get("avg_response_time", 0.0)
        
        metrics = PerformanceMetrics(
            total_requests=total_requests,
            total_failures=total_failures,
            total_success=total_success,
            avg_response_time=avg_response_time,
            min_response_time=min(response_times.values()) if response_times else 0.0,
            max_response_time=max(response_times.values()) if response_times else 0.0,
            median_response_time=self.calculate_statistics(
                list(response_times.values()) if response_times else []
            ).get("median", 0.0),
            p95_response_time=self.calculate_statistics(
                list(response_times.values()) if response_times else []
            ).get("p95", 0.0),
            p99_response_time=self.calculate_statistics(
                list(response_times.values()) if response_times else []
            ).get("p99", 0.0),
            requests_per_second=stats.get("total_rps", 0.0),
            failure_rate=total_failures / total_requests if total_requests > 0 else 0.0,
            concurrent_users=test_results.get("user_count", 0),
            test_duration=test_results.get("duration", 0.0),
        )
        
        return metrics
    
    def calculate_statistics(self, response_times: List[float]) -> Dict[str, float]:
        """
        计算响应时间统计数据
        
        Args:
            response_times: 响应时间列表
        
        Returns:
            统计数据字典，包含平均值、中位数、P95、P99等
        """
        if not response_times:
            return {
                "avg": 0.0,
                "median": 0.0,
                "p95": 0.0,
                "p99": 0.0,
                "min": 0.0,
                "max": 0.0,
                "std_dev": 0.0,
            }
        
        sorted_times = sorted(response_times)
        n = len(sorted_times)
        
        avg = statistics.mean(sorted_times)
        median = statistics.median(sorted_times)
        
        p95_index = int(n * 0.95)
        p99_index = int(n * 0.99)
        
        p95 = sorted_times[min(p95_index, n - 1)]
        p99 = sorted_times[min(p99_index, n - 1)]
        
        std_dev = statistics.stdev(sorted_times) if n > 1 else 0.0
        
        return {
            "avg": avg,
            "median": median,
            "p95": p95,
            "p99": p99,
            "min": min(sorted_times),
            "max": max(sorted_times),
            "std_dev": std_dev,
        }
    
    def check_performance_benchmark(self, results: Dict[str, Any]) -> Dict[str, Any]:
        """
        检查性能基准
        
        Args:
            results: 测试结果数据
        
        Returns:
            基准检查结果
        """
        if not self.benchmark_config:
            return {"status": "no_benchmark_configured"}
        
        stats = results.get("stats", {})
        response_times = stats.get("response_times", {})
        
        response_time_values = list(response_times.values()) if response_times else []
        stats_data = self.calculate_statistics(response_time_values)
        
        avg_time = stats_data["avg"]
        p95_time = stats_data["p95"]
        p99_time = stats_data["p99"]
        
        total_requests = stats.get("num_requests", 0)
        total_failures = stats.get("num_failures", 0)
        error_rate = total_failures / total_requests if total_requests > 0 else 0.0
        
        throughput = stats.get("total_rps", 0.0)
        
        response_time_check = self.benchmark_config.check_response_time(
            avg_time, p95_time, p99_time
        )
        throughput_check = self.benchmark_config.check_throughput(throughput)
        error_rate_check = self.benchmark_config.check_error_rate(error_rate)
        
        all_passed = (
            all(response_time_check.values())
            and throughput_check
            and error_rate_check
        )
        
        return {
            "status": "passed" if all_passed else "failed",
            "response_time": {
                "avg": {"value": avg_time, "threshold": self.benchmark_config.response_time_avg_threshold, "passed": response_time_check["avg_time_ok"]},
                "p95": {"value": p95_time, "threshold": self.benchmark_config.response_time_p95_threshold, "passed": response_time_check["p95_time_ok"]},
                "p99": {"value": p99_time, "threshold": self.benchmark_config.response_time_p99_threshold, "passed": response_time_check["p99_time_ok"]},
            },
            "throughput": {
                "value": throughput,
                "threshold": self.benchmark_config.throughput_threshold,
                "passed": throughput_check,
            },
            "error_rate": {
                "value": error_rate,
                "threshold": self.benchmark_config.error_rate_threshold,
                "passed": error_rate_check,
            },
        }
    
    def _analyze_bottlenecks(
        self, metrics: PerformanceMetrics, benchmark_results: Dict[str, Any]
    ) -> List[str]:
        """
        分析性能瓶颈
        
        Args:
            metrics: 性能指标
            benchmark_results: 基准检查结果
        
        Returns:
            瓶颈列表
        """
        bottlenecks = []
        
        if metrics.avg_response_time > 500:
            bottlenecks.append(f"平均响应时间过高: {metrics.avg_response_time:.2f}ms")
        
        if metrics.p95_response_time > 1500:
            bottlenecks.append(f"P95响应时间过高: {metrics.p95_response_time:.2f}ms")
        
        if metrics.p99_response_time > 2000:
            bottlenecks.append(f"P99响应时间过高: {metrics.p99_response_time:.2f}ms")
        
        if metrics.requests_per_second < 100:
            bottlenecks.append(f"吞吐量不足: {metrics.requests_per_second:.2f} req/s")
        
        if metrics.failure_rate > 0.01:
            bottlenecks.append(f"错误率过高: {metrics.failure_rate * 100:.2f}%")
        
        if metrics.concurrent_users < 50 and metrics.avg_response_time > 300:
            bottlenecks.append("低并发下响应时间仍然较高，可能存在代码性能问题")
        
        if metrics.max_response_time > metrics.avg_response_time * 10:
            bottlenecks.append("最大响应时间远超平均值，可能存在偶发性性能问题")
        
        return bottlenecks
    
    def _generate_recommendations(
        self, metrics: PerformanceMetrics, bottlenecks: List[str]
    ) -> List[str]:
        """
        生成性能优化建议
        
        Args:
            metrics: 性能指标
            bottlenecks: 瓶颈列表
        
        Returns:
            优化建议列表
        """
        recommendations = []
        
        if metrics.avg_response_time > 300:
            recommendations.append("优化数据库查询，添加必要的索引")
            recommendations.append("考虑使用缓存机制（如Redis）减少数据库访问")
            recommendations.append("检查是否存在N+1查询问题")
        
        if metrics.requests_per_second < 100:
            recommendations.append("增加服务器资源或优化应用性能")
            recommendations.append("考虑使用负载均衡分散流量")
            recommendations.append("优化数据库连接池配置")
        
        if metrics.failure_rate > 0.01:
            recommendations.append("检查错误日志，定位失败原因")
            recommendations.append("增加重试机制和错误处理")
            recommendations.append("检查服务超时配置是否合理")
        
        if metrics.concurrent_users > 80 and metrics.avg_response_time > 400:
            recommendations.append("考虑水平扩展，增加服务实例")
            recommendations.append("优化并发处理机制")
            recommendations.append("检查是否存在资源竞争问题")
        
        if metrics.p99_response_time > metrics.p95_response_time * 1.5:
            recommendations.append("检查是否存在长尾请求问题")
            recommendations.append("考虑对慢请求进行专项优化")
            recommendations.append("添加请求超时和熔断机制")
        
        if not bottlenecks:
            recommendations.append("性能表现良好，继续保持监控")
        
        return recommendations
    
    def generate_charts(self, results: Dict[str, Any]) -> Dict[str, Any]:
        """
        生成性能图表数据
        
        Args:
            results: 测试结果数据
        
        Returns:
            图表数据字典
        """
        stats = results.get("stats", {})
        response_times = stats.get("response_times", {})
        
        response_time_values = list(response_times.values()) if response_times else []
        
        charts = {
            "response_time_distribution": self._generate_response_time_distribution(
                response_time_values
            ),
            "requests_per_second_over_time": self._generate_rps_chart(results),
            "error_rate_over_time": self._generate_error_rate_chart(results),
            "concurrent_users_over_time": self._generate_users_chart(results),
        }
        
        return charts
    
    def _generate_response_time_distribution(
        self, response_times: List[float]
    ) -> Dict[str, Any]:
        """
        生成响应时间分布图表数据
        
        Args:
            response_times: 响应时间列表
        
        Returns:
            图表数据
        """
        if not response_times:
            return {"labels": [], "data": []}
        
        bins = [0, 100, 200, 300, 500, 800, 1000, 1500, 2000, 3000, 5000, float("inf")]
        labels = ["0-100", "100-200", "200-300", "300-500", "500-800", "800-1000", "1000-1500", "1500-2000", "2000-3000", "3000-5000", "5000+"]
        
        distribution = [0] * (len(bins) - 1)
        
        for time in response_times:
            for i in range(len(bins) - 1):
                if bins[i] <= time < bins[i + 1]:
                    distribution[i] += 1
                    break
        
        return {
            "labels": labels,
            "data": distribution,
        }
    
    def _generate_rps_chart(self, results: Dict[str, Any]) -> Dict[str, Any]:
        """
        生成每秒请求数图表数据
        
        Args:
            results: 测试结果数据
        
        Returns:
            图表数据
        """
        history = results.get("history", [])
        
        if not history:
            return {"labels": [], "data": []}
        
        labels = [entry.get("timestamp", "") for entry in history]
        data = [entry.get("rps", 0) for entry in history]
        
        return {"labels": labels, "data": data}
    
    def _generate_error_rate_chart(self, results: Dict[str, Any]) -> Dict[str, Any]:
        """
        生成错误率图表数据
        
        Args:
            results: 测试结果数据
        
        Returns:
            图表数据
        """
        history = results.get("history", [])
        
        if not history:
            return {"labels": [], "data": []}
        
        labels = [entry.get("timestamp", "") for entry in history]
        data = [entry.get("fail_ratio", 0) * 100 for entry in history]
        
        return {"labels": labels, "data": data}
    
    def _generate_users_chart(self, results: Dict[str, Any]) -> Dict[str, Any]:
        """
        生成并发用户数图表数据
        
        Args:
            results: 测试结果数据
        
        Returns:
            图表数据
        """
        history = results.get("history", [])
        
        if not history:
            return {"labels": [], "data": []}
        
        labels = [entry.get("timestamp", "") for entry in history]
        data = [entry.get("user_count", 0) for entry in history]
        
        return {"labels": labels, "data": data}
    
    def export_to_html(self, report: PerformanceReport, output_path: str) -> str:
        """
        导出HTML报告
        
        Args:
            report: 性能测试报告
            output_path: 输出文件路径
        
        Returns:
            输出文件路径
        """
        html_content = self._generate_html_report(report)
        
        os.makedirs(os.path.dirname(output_path), exist_ok=True)
        
        with open(output_path, "w", encoding="utf-8") as f:
            f.write(html_content)
        
        return output_path
    
    def _generate_html_report(self, report: PerformanceReport) -> str:
        """
        生成HTML报告内容
        
        Args:
            report: 性能测试报告
        
        Returns:
            HTML内容字符串
        """
        html = f"""
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>性能测试报告 - {report.test_name}</title>
    <style>
        body {{
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }}
        .container {{
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }}
        h1 {{
            color: #2c3e50;
            border-bottom: 3px solid #3498db;
            padding-bottom: 10px;
        }}
        h2 {{
            color: #34495e;
            margin-top: 30px;
            border-left: 4px solid #3498db;
            padding-left: 10px;
        }}
        .metrics-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin: 20px 0;
        }}
        .metric-card {{
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 6px;
            border-left: 4px solid #3498db;
        }}
        .metric-card h3 {{
            margin-top: 0;
            color: #2c3e50;
        }}
        .metric-value {{
            font-size: 28px;
            font-weight: bold;
            color: #3498db;
        }}
        .status-passed {{
            color: #27ae60;
            font-weight: bold;
        }}
        .status-failed {{
            color: #e74c3c;
            font-weight: bold;
        }}
        .bottleneck-list, .recommendation-list {{
            list-style-type: none;
            padding-left: 0;
        }}
        .bottleneck-list li, .recommendation-list li {{
            padding: 10px;
            margin: 5px 0;
            background-color: #fff;
            border-left: 4px solid #e74c3c;
            border-radius: 4px;
        }}
        .recommendation-list li {{
            border-left-color: #27ae60;
        }}
        table {{
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }}
        th, td {{
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }}
        th {{
            background-color: #3498db;
            color: white;
        }}
        tr:hover {{
            background-color: #f5f5f5;
        }}
        .timestamp {{
            color: #7f8c8d;
            font-size: 0.9em;
        }}
    </style>
</head>
<body>
    <div class="container">
        <h1>性能测试报告</h1>
        <div class="timestamp">
            <p><strong>测试名称:</strong> {report.test_name}</p>
            <p><strong>开始时间:</strong> {report.test_start_time}</p>
            <p><strong>结束时间:</strong> {report.test_end_time}</p>
            <p><strong>测试时长:</strong> {report.test_duration:.2f} 秒</p>
        </div>
        
        <h2>性能指标概览</h2>
        <div class="metrics-grid">
            <div class="metric-card">
                <h3>总请求数</h3>
                <div class="metric-value">{report.metrics.total_requests:,}</div>
            </div>
            <div class="metric-card">
                <h3>成功请求</h3>
                <div class="metric-value">{report.metrics.total_success:,}</div>
            </div>
            <div class="metric-card">
                <h3>失败请求</h3>
                <div class="metric-value">{report.metrics.total_failures:,}</div>
            </div>
            <div class="metric-card">
                <h3>错误率</h3>
                <div class="metric-value">{report.metrics.failure_rate * 100:.2f}%</div>
            </div>
            <div class="metric-card">
                <h3>平均响应时间</h3>
                <div class="metric-value">{report.metrics.avg_response_time:.2f} ms</div>
            </div>
            <div class="metric-card">
                <h3>中位数响应时间</h3>
                <div class="metric-value">{report.metrics.median_response_time:.2f} ms</div>
            </div>
            <div class="metric-card">
                <h3>P95响应时间</h3>
                <div class="metric-value">{report.metrics.p95_response_time:.2f} ms</div>
            </div>
            <div class="metric-card">
                <h3>P99响应时间</h3>
                <div class="metric-value">{report.metrics.p99_response_time:.2f} ms</div>
            </div>
            <div class="metric-card">
                <h3>吞吐量</h3>
                <div class="metric-value">{report.metrics.requests_per_second:.2f} req/s</div>
            </div>
            <div class="metric-card">
                <h3>并发用户数</h3>
                <div class="metric-value">{report.metrics.concurrent_users}</div>
            </div>
        </div>
        
        <h2>性能基准检查</h2>
        <div class="status-{report.benchmark_results.get('status', 'unknown')}">
            <h3>状态: {report.benchmark_results.get('status', 'unknown').upper()}</h3>
        </div>
        
        <h2>性能瓶颈分析</h2>
        <ul class="bottleneck-list">
            {''.join(f'<li>{bottleneck}</li>' for bottleneck in report.bottlenecks) if report.bottlenecks else '<li>未发现明显性能瓶颈</li>'}
        </ul>
        
        <h2>优化建议</h2>
        <ul class="recommendation-list">
            {''.join(f'<li>{recommendation}</li>' for recommendation in report.recommendations)}
        </ul>
        
        <h2>测试配置</h2>
        <table>
            <thead>
                <tr>
                    <th>配置项</th>
                    <th>值</th>
                </tr>
            </thead>
            <tbody>
                {''.join(f'<tr><td>{key}</td><td>{value}</td></tr>' for key, value in report.config.items())}
            </tbody>
        </table>
        
        <div class="timestamp">
            <p>报告生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
        </div>
    </div>
</body>
</html>
        """
        
        return html
    
    def export_to_json(self, report: PerformanceReport, output_path: str) -> str:
        """
        导出JSON报告
        
        Args:
            report: 性能测试报告
            output_path: 输出文件路径
        
        Returns:
            输出文件路径
        """
        report_dict = {
            "test_name": report.test_name,
            "test_start_time": report.test_start_time,
            "test_end_time": report.test_end_time,
            "test_duration": report.test_duration,
            "config": report.config,
            "metrics": asdict(report.metrics),
            "benchmark_results": report.benchmark_results,
            "bottlenecks": report.bottlenecks,
            "recommendations": report.recommendations,
            "detailed_stats": report.detailed_stats,
            "generated_at": datetime.now().isoformat(),
        }
        
        os.makedirs(os.path.dirname(output_path), exist_ok=True)
        
        with open(output_path, "w", encoding="utf-8") as f:
            json.dump(report_dict, f, indent=2, ensure_ascii=False)
        
        return output_path
    
    def compare_reports(
        self, report1: PerformanceReport, report2: PerformanceReport
    ) -> Dict[str, Any]:
        """
        比较两个性能测试报告
        
        Args:
            report1: 第一个报告
            report2: 第二个报告
        
        Returns:
            比较结果
        """
        comparison = {
            "test_names": [report1.test_name, report2.test_name],
            "metrics_comparison": {},
            "improvement": {},
            "regression": {},
        }
        
        metrics1 = asdict(report1.metrics)
        metrics2 = asdict(report2.metrics)
        
        for key in metrics1.keys():
            if key in metrics2:
                value1 = metrics1[key]
                value2 = metrics2[key]
                
                if isinstance(value1, (int, float)) and isinstance(value2, (int, float)):
                    change = value2 - value1
                    change_percent = (change / value1 * 100) if value1 != 0 else 0
                    
                    comparison["metrics_comparison"][key] = {
                        "before": value1,
                        "after": value2,
                        "change": change,
                        "change_percent": change_percent,
                    }
                    
                    if key in ["avg_response_time", "p95_response_time", "p99_response_time", "failure_rate"]:
                        if change < 0:
                            comparison["improvement"][key] = abs(change_percent)
                    elif key in ["requests_per_second", "total_success", "concurrent_users"]:
                        if change > 0:
                            comparison["improvement"][key] = abs(change_percent)
        
        return comparison


def create_test_results_from_locust(
    locust_stats: Dict[str, Any],
    test_name: str,
    config: Dict[str, Any],
) -> Dict[str, Any]:
    """
    从Locust统计数据创建测试结果
    
    Args:
        locust_stats: Locust统计数据
        test_name: 测试名称
        config: 测试配置
    
    Returns:
        测试结果字典
    """
    return {
        "test_name": test_name,
        "start_time": locust_stats.get("start_time", ""),
        "end_time": locust_stats.get("end_time", ""),
        "duration": locust_stats.get("duration", 0.0),
        "user_count": locust_stats.get("user_count", 0),
        "config": config,
        "stats": locust_stats.get("stats", {}),
        "history": locust_stats.get("history", []),
    }
