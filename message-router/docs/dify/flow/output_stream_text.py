def main(content: str) -> dict:
    return {
        "stream_out_type": "text",
        "content": content
    }

# 测试用例
if __name__ == '__main__':
    import unittest
    
    class TestOutputStreamText(unittest.TestCase):
        def test_main_function(self):
            # 测试用例1：普通文本
            test_content = "Hello, World!"
            result = main(test_content)
            self.assertEqual(result["stream_out_type"], "text")
            self.assertEqual(result["content"], test_content)
            
            # 测试用例2：空字符串
            test_content = ""
            result = main(test_content)
            self.assertEqual(result["stream_out_type"], "text")
            self.assertEqual(result["content"], "")
            
            # 测试用例3：包含特殊字符的文本
            test_content = "Hello\nWorld\t123!@#"
            result = main(test_content)
            self.assertEqual(result["stream_out_type"], "text")
            self.assertEqual(result["content"], test_content)
    
    unittest.main() 