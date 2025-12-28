import ForgotPasswordClient from "@/components/commons/auth/ForgotPasswordClient";

export default function ForgotPasswordPage() {
  return <ForgotPasswordClient />;
}

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setIdentifier(e.target.value);
    if (error) setError("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const res = await sendOTP(identifier);

    if (!res) {
      return;
    }

    toast.success("Đã gửi mã OTP về email của bạn");

    router.push(
      `/auth/verification?identifier=${encodeURIComponent(
        identifier
      )}&isActivation=false`
    );
  };

  return (
    <div className="space-y-6">
      <div className="space-y-2 text-center">
        <h1 className="text-2xl font-bold tracking-tight">Quên mật khẩu</h1>
        <p className="text-muted-foreground">
          Nhập email của bạn và chúng tôi sẽ gửi mã OTP để giúp bạn cài lại mật
          khẩu mới
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="identifier">Email hoặc username</Label>
          <div className="relative">
            <Mail className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              id="identifier"
              type="text"
              name="identifier"
              placeholder="Nhập email hoặc username của bạn"
              value={identifier}
              onChange={handleChange}
              className="pl-10"
            />
          </div>
          {error && (
            <Alert variant="destructive">
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
        </div>

        <Button type="submit" className="w-full" disabled={isLoading}>
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Đang gửi...
            </>
          ) : (
            <>
              <Send className="mr-2 h-4 w-4" />
              Gửi mã OTP
            </>
          )}
        </Button>
      </form>

      <div className="text-center">
        <Link
          href="/auth/login"
          className="inline-flex items-center gap-2 text-sm text-primary hover:underline"
        >
          <ArrowLeft className="h-3 w-3" />
          Quay lại trang đăng nhập
        </Link>
      </div>
    </div>
  );
};

export default ForgotPasswordPage;
